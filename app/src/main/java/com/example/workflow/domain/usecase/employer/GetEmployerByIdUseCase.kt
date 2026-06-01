package com.example.workflow.domain.usecase.employer

import com.example.workflow.domain.repository.EmployerRepository

import javax.inject.Inject

class GetEmployerByIdUseCase @Inject constructor(private val repository: EmployerRepository) {
    suspend operator fun invoke(employerId: String) = repository.getById(employerId)
}
