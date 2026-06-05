package com.example.workflow.presentation.screens.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerVacanciesViewModel @Inject constructor(
    private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val vacancies: List<Vacancy>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var employerId: String = ""

    init {
        viewModelScope.launch {
            employerId = tokenDataStore.getUserId() ?: ""
            loadVacancies()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { getEmployerVacanciesUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it.reversed()) }
            _isRefreshing.value = false
        }
    }

    fun loadVacancies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getEmployerVacanciesUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it.reversed()) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }
}
