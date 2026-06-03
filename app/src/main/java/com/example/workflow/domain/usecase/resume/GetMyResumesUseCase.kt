package com.example.workflow.domain.usecase.resume

import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.repository.ResumeRepository
import javax.inject.Inject

class GetMyResumesUseCase @Inject constructor(
    private val repository: ResumeRepository
) {
    suspend operator fun invoke(seekerId: String): List<ResumeResponseDto> {
        return repository.getMyResumes(seekerId)
    }
}
