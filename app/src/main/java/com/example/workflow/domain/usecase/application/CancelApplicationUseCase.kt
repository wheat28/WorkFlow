package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository

class CancelApplicationUseCase(private val repository: ApplicationRepository) {
    suspend operator fun invoke(applicationId: String) = repository.cancel(applicationId)
}
