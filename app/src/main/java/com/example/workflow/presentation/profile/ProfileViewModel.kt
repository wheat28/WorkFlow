package com.example.workflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.resume.GetMyResumesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getMyResumesUseCase: GetMyResumesUseCase,
    private val seekerId: String
) : ViewModel() {

    sealed class ResumeState {
        object Loading : ResumeState()
        data class Success(val resumes: List<ResumeResponseDto>) : ResumeState()
        data class Error(val message: String) : ResumeState()
    }

    private val _resumeState = MutableStateFlow<ResumeState>(ResumeState.Loading)
    val resumeState: StateFlow<ResumeState> = _resumeState

    init { loadResumes() }

    fun loadResumes() {
        viewModelScope.launch {
            _resumeState.value = ResumeState.Loading
            runCatching { getMyResumesUseCase(seekerId) }
                .onSuccess { _resumeState.value = ResumeState.Success(it) }
                .onFailure { _resumeState.value = ResumeState.Error(it.message ?: "Ошибка") }
        }
    }

    class Factory(
        private val getMyResumesUseCase: GetMyResumesUseCase,
        private val seekerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            ProfileViewModel(getMyResumesUseCase, seekerId) as T
    }
}
