package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.domain.repository.ResumeRepository

import javax.inject.Inject

class UpdateResumeUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(id: String, request: ResumeRequestDto) {
        return repository.updateResume(id, request)
    }
}
