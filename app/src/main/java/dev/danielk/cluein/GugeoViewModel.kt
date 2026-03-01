package dev.danielk.cluein

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CorrectionState {
    object Idle : CorrectionState()
    object Loading : CorrectionState()
    data class Success(val result: CorrectionResult) : CorrectionState()
    data class Error(val message: String) : CorrectionState()
}

class GugeoViewModel(private val engine: GugeoEngine) : ViewModel() {

    private val _state = MutableStateFlow<CorrectionState>(CorrectionState.Idle)
    val state: StateFlow<CorrectionState> = _state

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _markings = MutableStateFlow<List<ConfidenceMarking>>(emptyList())
    val markings: StateFlow<List<ConfidenceMarking>> = _markings

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
                _state.value = CorrectionState.Success(response.toCorrectionResult(request.originalText))
            } catch (e: Exception) {
                _state.value = CorrectionState.Error(e.message ?: "알 수 없는 오류")
            }
        }
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
            @Suppress("UNCHECKED_CAST")
            return GugeoViewModel(engine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
