package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.data.remote.dto.ApplicationResponseDto

interface ApplicationRepository {
    suspend fun apply(request: ApplicationRequestDto): String
    suspend fun getMyApplications(seekerId: String): List<ApplicationResponseDto>
    suspend fun isApplied(vacancyId: String): Boolean
    suspend fun getByVacancyId(vacancyId: String): List<ApplicationResponseDto>
    suspend fun updateStatus(applicationId: String, status: String)
}
