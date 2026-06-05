package com.example.workflow.presentation.screens.apply

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.usecase.application.ApplyForVacancyUseCase
import com.example.workflow.domain.usecase.resume.GetMyResumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyViewModel @Inject constructor(
    private val getMyResumesUseCase: GetMyResumesUseCase,
    private val applyForVacancyUseCase: ApplyForVacancyUseCase,
    private val tokenDataStore: TokenDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vacancyId: String = checkNotNull(savedStateHandle["vacancyId"])

    sealed class UiState {
        object Loading : UiState()
        data class Ready(val resumes: List<Resume>) : UiState()
        object Submitting : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            val seekerId = tokenDataStore.getUserId() ?: ""
            loadResumes(seekerId)
        }
    }

    private fun loadResumes(seekerId: String) {
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
}
