package com.example.workflow.domain.usecase.employer

import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import com.example.workflow.domain.repository.EmployerRepository

class UpdateEmployerUseCase(private val repository: EmployerRepository) {
    suspend operator fun invoke(employerId: String, request: EmployerUpdateRequestDto) =
        repository.updateProfile(employerId, request)
}
