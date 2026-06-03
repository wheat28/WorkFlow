package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.domain.repository.ResumeRepository
import javax.inject.Inject

class CreateResumeUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(request: ResumeRequestDto): String {
        return repository.createResume(request)
    }
}
