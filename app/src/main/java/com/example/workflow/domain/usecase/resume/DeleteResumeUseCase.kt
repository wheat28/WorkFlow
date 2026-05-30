package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.repository.ResumeRepository

class DeleteResumeUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(id: String) = repository.deleteResume(id)
}
