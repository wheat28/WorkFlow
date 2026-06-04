package com.example.workflow.domain.repository

import com.example.workflow.domain.model.Application

interface ApplicationRepository {
    suspend fun apply(vacancyId: String, resumeId: String, coverLetter: String?): String
    suspend fun getMyApplications(seekerId: String): List<Application>
    suspend fun isApplied(vacancyId: String): Boolean
    suspend fun getByVacancyId(vacancyId: String): List<Application>
    suspend fun updateStatus(applicationId: String, status: String)
    suspend fun cancel(applicationId: String)
}
