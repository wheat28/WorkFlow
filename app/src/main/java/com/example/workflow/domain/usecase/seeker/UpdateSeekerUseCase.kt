package com.example.workflow.domain.usecase.seeker

import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.repository.SeekerRepository

import javax.inject.Inject

class UpdateSeekerUseCase @Inject constructor(private val repository: SeekerRepository) {
    suspend operator fun invoke(seekerId: String, request: SeekerUpdateRequestDto) =
        repository.updateProfile(seekerId, request)
}
