package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.EmployerApi
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import com.example.workflow.domain.repository.EmployerRepository

class EmployerRepositoryImpl(
    private val api: EmployerApi,
    private val tokenDataStore: TokenDataStore
) : EmployerRepository {

    override suspend fun getById(employerId: String): EmployerResponseDto {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getById(token, employerId)
    }

    override suspend fun updateProfile(employerId: String, request: EmployerUpdateRequestDto) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.updateProfile(token, employerId, request)
    }

    override suspend fun getStats(employerId: String): EmployerStatsDto {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getStats(token, employerId)
    }
}
