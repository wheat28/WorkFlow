package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.domain.usecase.vacancy.CreateVacancyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateVacancyViewModel(
    private val createVacancyUseCase: CreateVacancyUseCase
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
        description: String,
        employmentType: String,
        experience: String,
        city: String,
        salaryFrom: String,
        salaryTo: String,
        currency: String
    ) {
        if (title.isBlank() || description.isBlank()) {
            _uiState.value = UiState.Error("Заполните название и описание")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching {
                createVacancyUseCase(
                    VacancyRequestDto(
                        title = title.trim(),
                        description = description.trim(),
                        employmentType = employmentType,
                        experience = experience,
                        city = city.trim().ifBlank { null },
                        salaryFrom = salaryFrom.trim().toIntOrNull(),
                        salaryTo = salaryTo.trim().toIntOrNull(),
                        currency = currency.trim().ifBlank { "RUB" }
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

    class Factory(private val useCase: CreateVacancyUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            CreateVacancyViewModel(useCase) as T
    }
}
