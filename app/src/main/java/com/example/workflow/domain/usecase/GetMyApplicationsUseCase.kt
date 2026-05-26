package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.ApplicationRepository

class GetMyApplicationsUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(seekerId: String) = repository.getMyApplications(seekerId)
}
