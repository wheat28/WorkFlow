package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.repository.ResumeRepository

class SetResumeActiveUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(id: String, isActive: Boolean) = repository.setResumeActive(id, isActive)
}
