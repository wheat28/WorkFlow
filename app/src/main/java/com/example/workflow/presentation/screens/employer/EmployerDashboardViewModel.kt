package com.example.workflow.presentation.screens.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.EmployerStats
import com.example.workflow.domain.usecase.employer.GetEmployerStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerDashboardViewModel @Inject constructor(
    private val getEmployerStatsUseCase: GetEmployerStatsUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            val employerId = tokenDataStore.getUserId() ?: ""
            load(employerId)
        }
    }

    fun load(employerId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getEmployerStatsUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val stats: EmployerStats) : UiState()
        data class Error(val message: String) : UiState()
    }
}
