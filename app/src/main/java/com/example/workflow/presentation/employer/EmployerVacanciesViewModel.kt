package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployerVacanciesViewModel(
    private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    private val employerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val vacancies: List<VacancyResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init { loadVacancies() }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { getEmployerVacanciesUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
            _isRefreshing.value = false
        }
    }

    fun loadVacancies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getEmployerVacanciesUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    class Factory(
        private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
        private val employerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EmployerVacanciesViewModel(getEmployerVacanciesUseCase, employerId) as T
    }
}
