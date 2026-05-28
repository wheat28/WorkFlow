package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto

interface EmployerRepository {
    suspend fun getById(employerId: String): EmployerResponseDto
    suspend fun updateProfile(employerId: String, request: EmployerUpdateRequestDto)
    suspend fun getStats(employerId: String): EmployerStatsDto
}
