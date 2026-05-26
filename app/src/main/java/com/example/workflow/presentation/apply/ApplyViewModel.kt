package com.example.workflow.presentation.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.ApplyForVacancyUseCase
import com.example.workflow.domain.usecase.GetMyResumesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApplyViewModel(
    private val getMyResumesUseCase: GetMyResumesUseCase,
    private val applyForVacancyUseCase: ApplyForVacancyUseCase,
    private val seekerId: String,
    private val vacancyId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val resumes: List<ResumeResponseDto>) : UiState()
        object Submitting : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { loadResumes() }

    private fun loadResumes() {
        viewModelScope.launch {
            runCatching { getMyResumesUseCase(seekerId) }
                .onSuccess { _uiState.value = UiState.Ready(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun apply(resumeId: String, coverLetter: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Submitting
            runCatching { applyForVacancyUseCase(vacancyId, resumeId, coverLetter) }
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка отклика") }
        }
    }

    class Factory(
        private val getMyResumesUseCase: GetMyResumesUseCase,
        private val applyForVacancyUseCase: ApplyForVacancyUseCase,
        private val seekerId: String,
        private val vacancyId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            ApplyViewModel(getMyResumesUseCase, applyForVacancyUseCase, seekerId, vacancyId) as T
    }
}
