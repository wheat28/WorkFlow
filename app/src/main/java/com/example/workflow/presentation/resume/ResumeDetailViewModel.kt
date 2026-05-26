package com.example.workflow.presentation.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.GetResumeByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResumeDetailViewModel(
    private val getResumeByIdUseCase: GetResumeByIdUseCase,
    private val resumeId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val resume: ResumeResponseDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { getResumeByIdUseCase(resumeId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    class Factory(
        private val getResumeByIdUseCase: GetResumeByIdUseCase,
        private val resumeId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ResumeDetailViewModel(getResumeByIdUseCase, resumeId) as T
    }
}
