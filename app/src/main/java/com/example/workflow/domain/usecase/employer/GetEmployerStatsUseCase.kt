package com.example.workflow.domain.usecase.employer

import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.domain.repository.EmployerRepository
import javax.inject.Inject

class GetEmployerStatsUseCase @Inject constructor(
    private val repository: EmployerRepository
) {
    suspend operator fun invoke(employerId: String): EmployerStatsDto {
        return repository.getStats(employerId)
    }
}
