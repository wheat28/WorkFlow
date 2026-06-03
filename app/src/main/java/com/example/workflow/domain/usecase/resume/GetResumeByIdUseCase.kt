package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.repository.ResumeRepository

import javax.inject.Inject

class GetResumeByIdUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(id: String): ResumeResponseDto {
        return repository.getResumeById(id)
    }
}
