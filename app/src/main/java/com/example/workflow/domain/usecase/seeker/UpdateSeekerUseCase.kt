package com.example.workflow.domain.usecase.seeker

import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.repository.SeekerRepository

class UpdateSeekerUseCase(private val repository: SeekerRepository) {
    suspend operator fun invoke(seekerId: String, request: SeekerUpdateRequestDto) =
        repository.updateProfile(seekerId, request)
}
