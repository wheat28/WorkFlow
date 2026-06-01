package com.example.workflow.domain.usecase.auth

import com.example.workflow.domain.repository.AuthRepository

import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
