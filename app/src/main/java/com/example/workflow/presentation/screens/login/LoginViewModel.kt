package com.example.workflow.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            runCatching { loginUseCase(email, password) }
                .onSuccess { _uiState.value = LoginUiState.Success }
                .onFailure { _uiState.value = LoginUiState.Error(it.message ?: "Ошибка входа") }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }

    sealed class LoginUiState {
        object Idle : LoginUiState()
        object Loading : LoginUiState()
        object Success : LoginUiState()
        data class Error(
            val message: String
        ) : LoginUiState()
    }

}