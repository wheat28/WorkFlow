package com.example.workflow.domain.usecase.employer

import com.example.workflow.domain.model.EmployerStats
import com.example.workflow.domain.repository.EmployerRepository
import javax.inject.Inject

class GetEmployerStatsUseCase @Inject constructor(
    private val repository: EmployerRepository
) {
    suspend operator fun invoke(employerId: String): EmployerStats {
        return repository.getStats(employerId)
    }
}
