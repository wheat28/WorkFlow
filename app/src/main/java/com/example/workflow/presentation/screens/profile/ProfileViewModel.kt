package com.example.workflow.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.usecase.resume.GetMyResumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyResumesUseCase: GetMyResumesUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _resumeState = MutableStateFlow<ResumeState>(ResumeState.Loading)
    val resumeState: StateFlow<ResumeState> = _resumeState

    init {
        loadResumes()
    }

    fun loadResumes() {
        viewModelScope.launch {
            _resumeState.value = ResumeState.Loading
            val seekerId = tokenDataStore.getUserId() ?: ""
            runCatching { getMyResumesUseCase(seekerId) }
                .onSuccess { _resumeState.value = ResumeState.Success(it) }
                .onFailure { _resumeState.value = ResumeState.Error(it.message ?: "Ошибка") }
        }
    }

    sealed class ResumeState {
        object Loading : ResumeState()
        data class Success(val resumes: List<Resume>) : ResumeState()
        data class Error(val message: String) : ResumeState()
    }
}
