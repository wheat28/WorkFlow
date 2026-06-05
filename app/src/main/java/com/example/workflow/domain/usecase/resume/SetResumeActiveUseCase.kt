package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.repository.ResumeRepository

import javax.inject.Inject

class SetResumeActiveUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(id: String, isActive: Boolean) {
        return repository.setResumeActive(id, isActive)
    }
}
