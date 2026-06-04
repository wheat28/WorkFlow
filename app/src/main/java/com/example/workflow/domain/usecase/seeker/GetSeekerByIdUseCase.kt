package com.example.workflow.domain.usecase.seeker

import com.example.workflow.domain.model.Seeker
import com.example.workflow.domain.repository.SeekerRepository

import javax.inject.Inject

class GetSeekerByIdUseCase @Inject constructor(
    private val repository: SeekerRepository
) {
    suspend operator fun invoke(seekerId: String): Seeker {
        return repository.getById(seekerId)
    }
}
