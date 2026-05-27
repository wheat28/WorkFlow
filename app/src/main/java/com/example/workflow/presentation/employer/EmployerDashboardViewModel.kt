package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.domain.usecase.employer.GetEmployerStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployerDashboardViewModel(
    private val getEmployerStatsUseCase: GetEmployerStatsUseCase,
    private val employerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val stats: EmployerStatsDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getEmployerStatsUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    class Factory(
        private val getEmployerStatsUseCase: GetEmployerStatsUseCase,
        private val employerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            EmployerDashboardViewModel(getEmployerStatsUseCase, employerId) as T
    }
}
