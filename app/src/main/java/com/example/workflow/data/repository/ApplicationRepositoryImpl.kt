package com.example.workflow.data.repository

import com.example.workflow.data.remote.api.ApplicationApi
import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class ApplicationRepositoryImpl @Inject constructor(
    private val api: ApplicationApi
) : ApplicationRepository {

    override suspend fun apply(request: ApplicationRequestDto): String {
        return api.apply(request)
    }

    override suspend fun getMyApplications(seekerId: String): List<ApplicationResponseDto> {
        return api.getMyApplications(seekerId)
    }

    override suspend fun isApplied(vacancyId: String): Boolean {
        return api.checkApplied(vacancyId)
    }

    override suspend fun getByVacancyId(vacancyId: String): List<ApplicationResponseDto> {
        return api.getApplicationsByVacancy(vacancyId)
    }

    override suspend fun updateStatus(applicationId: String, status: String) {
        return api.updateApplicationStatus(applicationId, status)
    }

    override suspend fun cancel(applicationId: String) {
        return api.cancelApplication(applicationId)
    }
}
