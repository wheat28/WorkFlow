package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.model.ResumeInput
import com.example.workflow.domain.repository.ResumeRepository

import javax.inject.Inject

class UpdateResumeUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(id: String, input: ResumeInput) {
        return repository.updateResume(id, input)
    }
}
