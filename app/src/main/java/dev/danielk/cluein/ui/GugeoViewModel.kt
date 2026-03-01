package dev.danielk.cluein.ui

import android.content.Context
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.danielk.cluein.data.ApiKeyManager
import dev.danielk.cluein.data.AppDatabase
import dev.danielk.cluein.data.FakeGugeoEngine
import dev.danielk.cluein.data.GugeoEngineImpl
import dev.danielk.cluein.data.HistoryDao
import dev.danielk.cluein.data.HistoryEntity
import dev.danielk.cluein.domain.ChatMessage
import dev.danielk.cluein.domain.ConfidenceMarking
import dev.danielk.cluein.domain.CorrectionResult
import dev.danielk.cluein.domain.DummyData
import dev.danielk.cluein.domain.GugeoEngine
import dev.danielk.cluein.domain.GugeoRequest
import dev.danielk.cluein.domain.HistoryItem
import dev.danielk.cluein.domain.Sender
import dev.danielk.cluein.domain.SourceItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class CorrectionState {
    object Idle : CorrectionState()
    object Loading : CorrectionState()
    data class Success(val result: CorrectionResult) : CorrectionState()
    data class Error(val message: String) : CorrectionState()
}

class GugeoViewModel(
    private var engine: GugeoEngine,
    private val historyDao: HistoryDao
) : ViewModel() {

    private val gson = Gson()

    // 런타임에 엔진을 교체할 수 있도록 엔진 주입 방식을 개선합니다.
    fun updateEngine(newApiKey: String) {
        engine = GugeoEngineImpl(newApiKey)
    }

    fun updateToDummyEngine() {
        engine = FakeGugeoEngine()
    }

    private val _state = MutableStateFlow<CorrectionState>(CorrectionState.Idle)
    val state: StateFlow<CorrectionState> = _state

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _markings = MutableStateFlow<List<ConfidenceMarking>>(emptyList())
    val markings: StateFlow<List<ConfidenceMarking>> = _markings

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    // Room DB에서 이력을 가져와 UI용 HistoryItem으로 변환
    val history: StateFlow<List<HistoryItem>> = historyDao.getAllHistory()
        .map { entities ->
            entities.map { entity ->
                val sourcesType = object : TypeToken<List<SourceItem>>() {}.type
                val sources: List<SourceItem> = gson.fromJson(entity.sourcesJson, sourcesType)
                HistoryItem(
                    id = entity.id,
                    date = entity.date,
                    inputSummary = entity.inputSummary,
                    correctedSummary = entity.correctedSummary,
                    full = CorrectionResult(
                        originalText = entity.originalText,
                        correctedText = entity.correctedText,
                        explanation = entity.explanation,
                        probability = entity.probability,
                        sources = sources
                    )
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setInputText(text: String) {
        _inputText.value = text
        _markings.value = emptyList()
    }

    fun addMarking(marking: ConfidenceMarking) {
        if (_markings.value.none { it.text == marking.text }) {
            _markings.value = _markings.value + marking
        }
    }

    fun removeMarking(text: String) {
        _markings.value = _markings.value.filter { it.text != text }
    }

    fun setResultFromHistory(result: CorrectionResult) {
        _state.value = CorrectionState.Success(result)
        // 채팅 창에도 추가 (선택적)
        val userMsg = ChatMessage(UUID.randomUUID().toString(), result.originalText, dev.danielk.cluein.domain.Sender.USER)
        val botMsg = ChatMessage(UUID.randomUUID().toString(), result.correctedText, dev.danielk.cluein.domain.Sender.BOT, correctionResult = result)
        _chatMessages.value = listOf(userMsg, botMsg)
    }

    fun correct(request: GugeoRequest) {
        viewModelScope.launch {
            // 채팅 기록에 사용자 메시지 추가
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = request.originalText,
                sender = dev.danielk.cluein.domain.Sender.USER
            )
            _chatMessages.value = _chatMessages.value + userMessage
            _inputText.value = "" // 입력창 비우기

            _state.value = CorrectionState.Loading
            try {
                val response = engine.correct(request)
                val result = response.toCorrectionResult(request.originalText)
                _state.value = CorrectionState.Success(result)
                
                // 보정 성공 시 채팅 기록에 봇 메시지 추가
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = result.correctedText,
                    sender = dev.danielk.cluein.domain.Sender.BOT,
                    correctionResult = result
                )
                _chatMessages.value = _chatMessages.value + botMessage
                
                // 보정 성공 시 DB에 이력 저장
                saveToHistory(result)
            } catch (e: Exception) {
                _state.value = CorrectionState.Error(e.message ?: "알 수 없는 오류")
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = "죄송합니다. 오류가 발생했습니다: ${e.message}",
                    sender = dev.danielk.cluein.domain.Sender.BOT
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            }
        }
    }

    private suspend fun saveToHistory(result: CorrectionResult) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val entity = HistoryEntity(
            id = UUID.randomUUID().toString(),
            date = dateFormat.format(Date()),
            originalText = result.originalText,
            correctedText = result.correctedText,
            explanation = result.explanation,
            probability = result.probability,
            inputSummary = if (result.originalText.length > 20) result.originalText.take(20) + "…" else result.originalText,
            correctedSummary = if (result.correctedText.length > 20) result.correctedText.take(20) + "…" else result.correctedText,
            sourcesJson = gson.toJson(result.sources)
        )
        historyDao.insertHistory(entity)
    }
}

class GugeoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GugeoViewModel::class.java)) {
            val apiKey = ApiKeyManager.loadApiKey(context)
            val engine = if (apiKey != null) {
                GugeoEngineImpl(apiKey)
            } else {
                FakeGugeoEngine()
            }
            val database = AppDatabase.getDatabase(context)
            @Suppress("UNCHECKED_CAST")
            return GugeoViewModel(engine, database.historyDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
