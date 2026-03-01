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
import dev.danielk.cluein.domain.ConfidenceMarking
import dev.danielk.cluein.domain.CorrectionResult
import dev.danielk.cluein.domain.DummyData
import dev.danielk.cluein.domain.GugeoEngine
import dev.danielk.cluein.domain.GugeoRequest
import dev.danielk.cluein.domain.HistoryItem
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
    private val engine: GugeoEngine,
    private val historyDao: HistoryDao
) : ViewModel() {

    private val gson = Gson()

    private val _state = MutableStateFlow<CorrectionState>(CorrectionState.Idle)
    val state: StateFlow<CorrectionState> = _state

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _markings = MutableStateFlow<List<ConfidenceMarking>>(emptyList())
    val markings: StateFlow<List<ConfidenceMarking>> = _markings

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
        _markings.value = _markings.value + marking
    }

    fun setResultFromHistory(result: CorrectionResult) {
        _state.value = CorrectionState.Success(result)
    }

    fun correct(request: GugeoRequest) {
        viewModelScope.launch {
            _state.value = CorrectionState.Loading
            try {
                val response = engine.correct(request)
                val result = response.toCorrectionResult(request.originalText)
                _state.value = CorrectionState.Success(result)
                
                // 보정 성공 시 DB에 이력 저장
                saveToHistory(result)
            } catch (e: Exception) {
                _state.value = CorrectionState.Error(e.message ?: "알 수 없는 오류")
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
