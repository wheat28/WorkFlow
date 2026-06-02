package com.example.workflow.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.dto.ResumeResponseDto
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

    sealed class ResumeState {
        object Loading : ResumeState()
        data class Success(val resumes: List<ResumeResponseDto>) : ResumeState()
        data class Error(val message: String) : ResumeState()
    }

    private val _resumeState = MutableStateFlow<ResumeState>(ResumeState.Loading)
    val resumeState: StateFlow<ResumeState> = _resumeState

    private var seekerId: String = ""

    init {
        viewModelScope.launch {
            seekerId = tokenDataStore.getUserId() ?: ""
            loadResumes()
        }
    }

    fun loadResumes() {
        viewModelScope.launch {
            _resumeState.value = ResumeState.Loading
            runCatching { getMyResumesUseCase(seekerId) }
                .onSuccess { _resumeState.value = ResumeState.Success(it) }
                .onFailure { _resumeState.value = ResumeState.Error(it.message ?: "Ошибка") }
        }
    }
}
