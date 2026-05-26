package com.example.workflow.presentation.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.GetResumeByIdUseCase
import com.example.workflow.domain.usecase.UpdateResumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditResumeViewModel(
    private val getResumeByIdUseCase: GetResumeByIdUseCase,
    private val updateResumeUseCase: UpdateResumeUseCase,
    private val resumeId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val resume: ResumeResponseDto) : UiState()
        object Saving : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching { getResumeByIdUseCase(resumeId) }
                .onSuccess { _uiState.value = UiState.Ready(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun save(
        title: String,
        position: String,
        employmentType: String,
        salaryExpected: String,
        city: String,
        about: String
    ) {
        if (title.isBlank() || position.isBlank()) {
            _uiState.value = UiState.Error("Заполните название и должность")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Saving
            runCatching {
                updateResumeUseCase(
                    resumeId,
                    ResumeRequestDto(
                        title = title.trim(),
                        position = position.trim(),
                        employmentType = employmentType,
                        salaryExpected = salaryExpected.trim().toIntOrNull(),
                        city = city.trim().ifBlank { null },
                        about = about.trim().ifBlank { null }
                    )
                )
            }.onSuccess { _uiState.value = UiState.Success }
             .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка сохранения") }
        }
    }

    class Factory(
        private val getResumeByIdUseCase: GetResumeByIdUseCase,
        private val updateResumeUseCase: UpdateResumeUseCase,
        private val resumeId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            EditResumeViewModel(getResumeByIdUseCase, updateResumeUseCase, resumeId) as T
    }
}
