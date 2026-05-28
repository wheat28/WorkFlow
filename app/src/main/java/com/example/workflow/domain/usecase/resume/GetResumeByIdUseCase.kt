package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.repository.ResumeRepository

class GetResumeByIdUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(id: String) = repository.getResumeById(id)
}
