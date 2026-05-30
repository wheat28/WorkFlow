package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.employer.UpdateEmployerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditEmployerProfileViewModel(
    private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
    private val updateEmployerUseCase: UpdateEmployerUseCase,
    private val tokenDataStore: TokenDataStore,
    private val employerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val employer: EmployerResponseDto) : UiState()
        object Saving : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching { getEmployerByIdUseCase(employerId) }
                .onSuccess { _uiState.value = UiState.Ready(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun save(
        companyName: String,
        description: String,
        website: String,
        city: String,
        industry: String,
        phone: String
    ) {
        if (companyName.isBlank()) {
            _uiState.value = UiState.Error("Название компании обязательно")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Saving
            runCatching {
                updateEmployerUseCase(
                    employerId,
                    EmployerUpdateRequestDto(
                        companyName = companyName.trim(),
                        description = description.trim().ifBlank { null },
                        website = website.trim().ifBlank { null },
                        city = city.trim().ifBlank { null },
                        industry = industry.trim().ifBlank { null },
                        phone = phone.trim().ifBlank { null }
                    )
                )
                tokenDataStore.updateDisplayName(companyName.trim())
            }.onSuccess { _uiState.value = UiState.Success }
             .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка сохранения") }
        }
    }

    class Factory(
        private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
        private val updateEmployerUseCase: UpdateEmployerUseCase,
        private val tokenDataStore: TokenDataStore,
        private val employerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            EditEmployerProfileViewModel(getEmployerByIdUseCase, updateEmployerUseCase, tokenDataStore, employerId) as T
    }
}
