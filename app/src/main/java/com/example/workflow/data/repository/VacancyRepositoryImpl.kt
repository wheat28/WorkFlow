package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.VacancyApi
import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.repository.VacancyRepository

class VacancyRepositoryImpl(
    private val api: VacancyApi,
    private val tokenDataStore: TokenDataStore
) : VacancyRepository {

    override suspend fun getAllVacancies(): List<VacancyResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getAllVacancies(token)
    }

    override suspend fun getVacancyById(id: String): VacancyResponseDto {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getVacancyById(token, id)
    }

    override suspend fun getEmployerVacancies(employerId: String): List<VacancyResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getEmployerVacancies(token, employerId)
    }

    override suspend fun createVacancy(request: VacancyRequestDto): String {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.createVacancy(token, request)
    }

    override suspend fun updateVacancy(id: String, request: VacancyRequestDto) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.updateVacancy(token, id, request)
    }

    override suspend fun deleteVacancy(id: String) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.deleteVacancy(token, id)
    }
}
