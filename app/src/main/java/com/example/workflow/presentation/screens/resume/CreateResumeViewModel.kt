package com.example.workflow.presentation.screens.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.model.ResumeInput
import com.example.workflow.domain.usecase.resume.CreateResumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateResumeViewModel @Inject constructor(
    private val createResumeUseCase: CreateResumeUseCase
) : ViewModel() {

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
                    ResumeInput(
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
}
