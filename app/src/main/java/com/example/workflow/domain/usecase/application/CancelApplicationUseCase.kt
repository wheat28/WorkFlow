package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class CancelApplicationUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    suspend operator fun invoke(applicationId: String) {
        return repository.cancel(applicationId)
    }
}
