package com.example.workflow.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.GetVacancyApplicationsUseCase
import com.example.workflow.domain.usecase.UpdateApplicationStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VacancyApplicationsViewModel(
    private val getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val vacancyId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val applications: List<ApplicationResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getVacancyApplicationsUseCase(vacancyId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun updateStatus(applicationId: String, status: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = UiState.Success(current.applications.map {
            if (it.id == applicationId) it.copy(status = status) else it
        })
        viewModelScope.launch {
            runCatching { updateApplicationStatusUseCase(applicationId, status) }
                .onFailure { _uiState.value = current }
        }
    }

    class Factory(
        private val getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase,
        private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
        private val vacancyId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            VacancyApplicationsViewModel(
                getVacancyApplicationsUseCase, updateApplicationStatusUseCase, vacancyId
            ) as T
    }
}
