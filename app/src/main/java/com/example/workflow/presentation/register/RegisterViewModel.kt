package com.example.workflow.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.usecase.auth.RegisterEmployerUseCase
import com.example.workflow.domain.usecase.auth.RegisterSeekerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerSeekerUseCase: RegisterSeekerUseCase,
    private val registerEmployerUseCase: RegisterEmployerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerSeeker(email: String, password: String, firstName: String, lastName: String, phone: String, city: String) {
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _uiState.value = RegisterUiState.Error("Заполните обязательные поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            runCatching { registerSeekerUseCase(email, password, firstName, lastName, phone, city) }
                .onSuccess { _uiState.value = RegisterUiState.Success }
                .onFailure { _uiState.value = RegisterUiState.Error(it.message ?: "Ошибка регистрации") }
        }
    }

    fun registerEmployer(email: String, password: String, companyName: String, description: String, website: String, city: String, industry: String, phone: String) {
        if (email.isBlank() || password.isBlank() || companyName.isBlank()) {
            _uiState.value = RegisterUiState.Error("Заполните обязательные поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            runCatching { registerEmployerUseCase(email, password, companyName, description, website, city, industry, phone) }
                .onSuccess { _uiState.value = RegisterUiState.Success }
                .onFailure { _uiState.value = RegisterUiState.Error(it.message ?: "Ошибка регистрации") }
        }
    }

    fun resetState() { _uiState.value = RegisterUiState.Idle }

    class Factory(
        private val registerSeekerUseCase: RegisterSeekerUseCase,
        private val registerEmployerUseCase: RegisterEmployerUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RegisterViewModel(registerSeekerUseCase, registerEmployerUseCase) as T
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
