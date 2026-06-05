package com.example.workflow.presentation.screens.resume

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.usecase.resume.GetResumeByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeDetailViewModel @Inject constructor(
    private val getResumeByIdUseCase: GetResumeByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val resumeId: String = checkNotNull(savedStateHandle["resumeId"])

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { getResumeByIdUseCase(resumeId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val resume: Resume) : UiState()
        data class Error(val message: String) : UiState()
    }
}
