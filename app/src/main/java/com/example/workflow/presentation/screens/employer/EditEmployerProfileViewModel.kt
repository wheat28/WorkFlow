package com.example.workflow.presentation.screens.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.model.EmployerProfileInput
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.employer.UpdateEmployerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEmployerProfileViewModel @Inject constructor(
    private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
    private val updateEmployerUseCase: UpdateEmployerUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var employerId: String = ""

    init {
        viewModelScope.launch {
            employerId = tokenDataStore.getUserId() ?: ""
            load()
        }
    }

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
                    EmployerProfileInput(
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

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val employer: Employer) : UiState()
        object Saving : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}
