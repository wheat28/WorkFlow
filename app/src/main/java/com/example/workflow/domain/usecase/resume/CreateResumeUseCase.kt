package com.example.workflow.domain.usecase.resume

import com.example.workflow.domain.model.ResumeInput
import com.example.workflow.domain.repository.ResumeRepository
import javax.inject.Inject

class CreateResumeUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(input: ResumeInput): String {
        return repository.createResume(input)
    }
}
