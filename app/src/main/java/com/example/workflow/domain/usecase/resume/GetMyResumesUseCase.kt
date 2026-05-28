package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.repository.ResumeRepository

class GetMyResumesUseCase(private val repository: ResumeRepository) {
    suspend operator fun invoke(seekerId: String): List<ResumeResponseDto> =
        repository.getMyResumes(seekerId)
}
