package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.UpdateVacancyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditVacancyViewModel(
    private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
    private val updateVacancyUseCase: UpdateVacancyUseCase,
    private val deleteVacancyUseCase: DeleteVacancyUseCase,
    private val vacancyId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val vacancy: VacancyResponseDto) : UiState()
        data class Saving(val vacancy: VacancyResponseDto) : UiState()
        object Saved : UiState()
        object Deleted : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching { getVacancyByIdUseCase(vacancyId) }
                .onSuccess { _uiState.value = UiState.Ready(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun save(
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
        val vacancy = (_uiState.value as? UiState.Ready)?.vacancy ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Saving(vacancy)
            runCatching {
                updateVacancyUseCase(
                    vacancyId,
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
            }.onSuccess { _uiState.value = UiState.Saved }
             .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка сохранения") }
        }
    }

    fun delete() {
        val vacancy = (_uiState.value as? UiState.Ready)?.vacancy ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Saving(vacancy)
            runCatching { deleteVacancyUseCase(vacancyId) }
                .onSuccess { _uiState.value = UiState.Deleted }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка удаления") }
        }
    }

    class Factory(
        private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
        private val updateVacancyUseCase: UpdateVacancyUseCase,
        private val deleteVacancyUseCase: DeleteVacancyUseCase,
        private val vacancyId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EditVacancyViewModel(
                getVacancyByIdUseCase, updateVacancyUseCase, deleteVacancyUseCase, vacancyId
            ) as T
    }
}
