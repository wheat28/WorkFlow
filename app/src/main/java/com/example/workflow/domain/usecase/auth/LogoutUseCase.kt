package com.example.workflow.domain.usecase.auth

import com.example.workflow.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
