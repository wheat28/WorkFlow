package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.domain.repository.ResumeRepository

class CreateResumeUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(request: ResumeRequestDto): String =
        repository.createResume(request)
}
