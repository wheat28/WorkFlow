package com.example.workflow.domain.usecase.employer

import com.example.workflow.domain.repository.EmployerRepository

class GetEmployerByIdUseCase(private val repository: EmployerRepository) {
    suspend operator fun invoke(employerId: String) = repository.getById(employerId)
}
