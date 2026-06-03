package com.example.workflow.data.repository

import com.example.workflow.data.remote.api.EmployerApi
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import com.example.workflow.domain.repository.EmployerRepository
import javax.inject.Inject

class EmployerRepositoryImpl @Inject constructor(
    private val api: EmployerApi
) : EmployerRepository {

    override suspend fun getById(employerId: String): EmployerResponseDto {
        return api.getById(employerId)
    }

    override suspend fun updateProfile(employerId: String, request: EmployerUpdateRequestDto) {
        return api.updateProfile(employerId, request)
    }

    override suspend fun getStats(employerId: String): EmployerStatsDto {
        return api.getStats(employerId)
    }
}
