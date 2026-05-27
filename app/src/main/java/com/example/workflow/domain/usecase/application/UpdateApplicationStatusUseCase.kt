package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository

class UpdateApplicationStatusUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(applicationId: String, status: String) =
        repository.updateStatus(applicationId, status)
}
