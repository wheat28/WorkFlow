package com.example.workflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.usecase.seeker.GetSeekerByIdUseCase
import com.example.workflow.domain.usecase.seeker.UpdateSeekerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditSeekerProfileViewModel(
    private val getSeekerByIdUseCase: GetSeekerByIdUseCase,
    private val updateSeekerUseCase: UpdateSeekerUseCase,
    private val tokenDataStore: TokenDataStore,
    private val seekerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Ready : UiState()
        object Saving : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val city = MutableStateFlow("")
    val about = MutableStateFlow("")
    val email = MutableStateFlow("")

    init { load() }

    private fun load() {
        viewModelScope.launch {
            runCatching { getSeekerByIdUseCase(seekerId) }
                .onSuccess { seeker ->
                    firstName.value = seeker.firstName
                    lastName.value = seeker.lastName
                    phone.value = seeker.phone ?: ""
                    city.value = seeker.city ?: ""
                    about.value = seeker.about ?: ""
                    email.value = seeker.email
                    _uiState.value = UiState.Ready
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun save(firstName: String, lastName: String, phone: String, city: String, about: String) {
        if (firstName.isBlank() || lastName.isBlank()) {
            _uiState.value = UiState.Error("Имя и фамилия обязательны")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Saving
            runCatching {
                updateSeekerUseCase(
                    seekerId,
                    SeekerUpdateRequestDto(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        phone = phone.trim().takeIf { it.isNotBlank() },
                        city = city.trim().takeIf { it.isNotBlank() },
                        about = about.trim().takeIf { it.isNotBlank() }
                    )
                )
                tokenDataStore.updateDisplayName("${firstName.trim()} ${lastName.trim()}")
            }
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка сохранения") }
        }
    }

    class Factory(
        private val getSeekerByIdUseCase: GetSeekerByIdUseCase,
        private val updateSeekerUseCase: UpdateSeekerUseCase,
        private val tokenDataStore: TokenDataStore,
        private val seekerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            EditSeekerProfileViewModel(getSeekerByIdUseCase, updateSeekerUseCase, tokenDataStore, seekerId) as T
    }
}
