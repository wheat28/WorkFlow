package com.example.workflow.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.CancelApplicationUseCase
import com.example.workflow.domain.usecase.GetMyApplicationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyApplicationsViewModel(
    private val getMyApplicationsUseCase: GetMyApplicationsUseCase,
    private val cancelApplicationUseCase: CancelApplicationUseCase,
    private val seekerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val applications: List<ApplicationResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getMyApplicationsUseCase(seekerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun cancel(applicationId: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = UiState.Success(current.applications.filter { it.id != applicationId })
        viewModelScope.launch {
            runCatching { cancelApplicationUseCase(applicationId) }
                .onFailure { _uiState.value = current }
        }
    }

    class Factory(
        private val getMyApplicationsUseCase: GetMyApplicationsUseCase,
        private val cancelApplicationUseCase: CancelApplicationUseCase,
        private val seekerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            MyApplicationsViewModel(getMyApplicationsUseCase, cancelApplicationUseCase, seekerId) as T
    }
}
