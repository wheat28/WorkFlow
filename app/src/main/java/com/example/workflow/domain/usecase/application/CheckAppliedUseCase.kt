package com.example.workflow.domain.usecase.application

import com.example.workflow.domain.repository.ApplicationRepository

import javax.inject.Inject

class CheckAppliedUseCase @Inject constructor(private val repository: ApplicationRepository) {
    suspend operator fun invoke(vacancyId: String) = repository.isApplied(vacancyId)
}
