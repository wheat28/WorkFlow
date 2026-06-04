package com.example.workflow.domain.usecase.seeker

import com.example.workflow.domain.model.SeekerProfileInput
import com.example.workflow.domain.repository.SeekerRepository

import javax.inject.Inject

class UpdateSeekerUseCase @Inject constructor(
    private val repository: SeekerRepository
) {
    suspend operator fun invoke(seekerId: String, input: SeekerProfileInput) {
        return repository.updateProfile(seekerId, input)
    }
}
