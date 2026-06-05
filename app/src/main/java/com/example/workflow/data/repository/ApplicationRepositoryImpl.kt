package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.remote.api.ApplicationApi
import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.domain.model.Application
import com.example.workflow.domain.repository.ApplicationRepository
import javax.inject.Inject

class ApplicationRepositoryImpl @Inject constructor(
    private val api: ApplicationApi
) : ApplicationRepository {

    override suspend fun apply(vacancyId: String, resumeId: String, coverLetter: String?): String {
        return api.apply(ApplicationRequestDto(vacancyId, resumeId, coverLetter))
    }

    override suspend fun getMyApplications(seekerId: String): List<Application> {
        return api.getMyApplications(seekerId).map { it.toDomain() }
    }

    override suspend fun isApplied(vacancyId: String): Boolean {
        return api.checkApplied(vacancyId)
    }

    override suspend fun getByVacancyId(vacancyId: String): List<Application> {
        return api.getApplicationsByVacancy(vacancyId).map { it.toDomain() }
    }

    override suspend fun updateStatus(applicationId: String, status: String) {
        return api.updateApplicationStatus(applicationId, status)
    }

    override suspend fun cancel(applicationId: String) {
        return api.cancelApplication(applicationId)
    }
}
