package com.example.workflow.domain.usecase.seeker

import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.domain.repository.SeekerRepository

class GetSeekerByIdUseCase(private val repository: SeekerRepository) {
    suspend operator fun invoke(seekerId: String): SeekerResponseDto = repository.getById(seekerId)
}
