package com.example.workflow.presentation.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.domain.usecase.CreateResumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateResumeViewModel(private val createResumeUseCase: CreateResumeUseCase) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun create(
        title: String,
        position: String,
        employmentType: String,
        salaryExpected: String,
        city: String,
        about: String
    ) {
        if (title.isBlank() || position.isBlank()) {
            _uiState.value = UiState.Error("Заполните название и желаемую должность")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching {
                createResumeUseCase(
                    ResumeRequestDto(
                        title = title.trim(),
                        position = position.trim(),
                        employmentType = employmentType,
                        salaryExpected = salaryExpected.trim().toIntOrNull(),
                        city = city.trim().ifBlank { null },
                        about = about.trim().ifBlank { null }
                    )
                )
            }.onSuccess {
                _uiState.value = UiState.Success
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Ошибка")
            }
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }

    class Factory(private val useCase: CreateResumeUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            CreateResumeViewModel(useCase) as T
    }
}
