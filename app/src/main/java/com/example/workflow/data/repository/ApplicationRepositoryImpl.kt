package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.ApplicationApi
import com.example.workflow.data.remote.dto.ApplicationRequestDto
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.repository.ApplicationRepository

class ApplicationRepositoryImpl(
    private val api: ApplicationApi,
    private val tokenDataStore: TokenDataStore
) : ApplicationRepository {

    override suspend fun apply(request: ApplicationRequestDto): String {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.apply(token, request)
    }

    override suspend fun getMyApplications(seekerId: String): List<ApplicationResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getMyApplications(token, seekerId)
    }

    override suspend fun isApplied(vacancyId: String): Boolean {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.checkApplied(token, vacancyId)
    }

    override suspend fun getByVacancyId(vacancyId: String): List<ApplicationResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getApplicationsByVacancy(token, vacancyId)
    }

    override suspend fun updateStatus(applicationId: String, status: String) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.updateApplicationStatus(token, applicationId, status)
    }
}
