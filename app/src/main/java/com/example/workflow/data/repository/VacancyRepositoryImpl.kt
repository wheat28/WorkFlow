package com.example.workflow.data.repository

import com.example.workflow.data.remote.api.VacancyApi
import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.repository.VacancyRepository
import javax.inject.Inject

class VacancyRepositoryImpl @Inject constructor(
    private val api: VacancyApi
) : VacancyRepository {

    override suspend fun getAllVacancies(): List<VacancyResponseDto> {
        return api.getAllVacancies()
    }

    override suspend fun getVacancyById(id: String): VacancyResponseDto {
        return api.getVacancyById(id)
    }

    override suspend fun getEmployerVacancies(employerId: String): List<VacancyResponseDto> {
        return api.getEmployerVacancies(employerId)
    }

    override suspend fun createVacancy(request: VacancyRequestDto): String {
        return api.createVacancy(request)
    }

    override suspend fun updateVacancy(id: String, request: VacancyRequestDto) {
        return api.updateVacancy(id, request)
    }

    override suspend fun setVacancyActive(id: String, isActive: Boolean) {
        return api.setVacancyActive(id, isActive)
    }

    override suspend fun deleteVacancy(id: String) {
        return api.deleteVacancy(id)
    }
}
