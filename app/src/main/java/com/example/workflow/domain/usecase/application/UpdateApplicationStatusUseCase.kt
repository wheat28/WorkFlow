package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository

import javax.inject.Inject

class UpdateApplicationStatusUseCase @Inject constructor(private val repository: ApplicationRepository) {
    suspend operator fun invoke(applicationId: String, status: String) =
        repository.updateStatus(applicationId, status)
}
