package com.example.workflow.presentation.screens.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerProfileViewModel @Inject constructor(
    private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val employer: Employer) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var employerId: String = ""

    init {
        viewModelScope.launch {
            employerId = tokenDataStore.getUserId() ?: ""
            load()
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getEmployerByIdUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }
}
