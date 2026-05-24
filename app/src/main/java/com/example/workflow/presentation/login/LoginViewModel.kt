package com.example.workflow.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.model.UserRole
import com.example.workflow.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    sealed class LoginUiState {
        object Idle : LoginUiState()
        object Loading : LoginUiState()
        object Success : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, role: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching { loginUseCase(email, password, role) }
                .onSuccess { _uiState.value = LoginUiState.Success }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Ошибка входа") }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }

    class Factory(private val loginUseCase: LoginUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(loginUseCase) as T
    }
}
