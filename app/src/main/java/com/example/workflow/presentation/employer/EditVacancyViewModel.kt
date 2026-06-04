package com.example.workflow.presentation.employer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.model.VacancyInput
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.vacancy.SetVacancyActiveUseCase
import com.example.workflow.domain.usecase.vacancy.UpdateVacancyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditVacancyViewModel @Inject constructor(
    private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
    private val updateVacancyUseCase: UpdateVacancyUseCase,
    private val deleteVacancyUseCase: DeleteVacancyUseCase,
    private val setVacancyActiveUseCase: SetVacancyActiveUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vacancyId: String = checkNotNull(savedStateHandle["vacancyId"])

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val vacancy: Vacancy) : UiState()
        data class Saving(val vacancy: Vacancy) : UiState()
        object Saved : UiState()
        object Deleted : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isActive = MutableStateFlow(true)
    val isActive: StateFlow<Boolean> = _isActive

    private val _toggleError = MutableStateFlow<String?>(null)
    val toggleError: StateFlow<String?> = _toggleError

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching { getVacancyByIdUseCase(vacancyId) }
                .onSuccess {
                    _isActive.value = it.isActive
                    _uiState.value = UiState.Ready(it)
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun setActive(active: Boolean) {
        val previous = _isActive.value
        _isActive.value = active
        viewModelScope.launch {
            runCatching { setVacancyActiveUseCase(vacancyId, active) }
                .onFailure {
                    _isActive.value = previous
                    _toggleError.value = it.message ?: "Ошибка обновления статуса"
                }
        }
    }

    fun clearToggleError() { _toggleError.value = null }

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
                    VacancyInput(
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
}
