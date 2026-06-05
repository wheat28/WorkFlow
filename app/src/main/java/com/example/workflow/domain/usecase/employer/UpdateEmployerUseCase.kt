package com.example.workflow.domain.usecase.employer

import com.example.workflow.domain.model.EmployerProfileInput
import com.example.workflow.domain.repository.EmployerRepository
import javax.inject.Inject

class UpdateEmployerUseCase @Inject constructor(
    private val repository: EmployerRepository
) {
    suspend operator fun invoke(employerId: String, input: EmployerProfileInput) {
        return repository.updateProfile(employerId, input)
    }
}
