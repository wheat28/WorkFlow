package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.model.Application
import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class GetMyApplicationsUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    suspend operator fun invoke(seekerId: String): List<Application> {
        return repository.getMyApplications(seekerId)
    }
}
