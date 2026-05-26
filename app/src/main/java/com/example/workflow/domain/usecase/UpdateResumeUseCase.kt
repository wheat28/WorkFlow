package com.example.workflow.domain.usecase

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.domain.repository.ResumeRepository

class UpdateResumeUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(id: String, request: ResumeRequestDto) =
        repository.updateResume(id, request)
}
