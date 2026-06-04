package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.mapper.toDto
import com.example.workflow.data.remote.api.VacancyApi
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.model.VacancyInput
import com.example.workflow.domain.repository.VacancyRepository
import javax.inject.Inject

class VacancyRepositoryImpl @Inject constructor(
    private val api: VacancyApi
) : VacancyRepository {

    override suspend fun getAllVacancies(): List<Vacancy> {
        return api.getAllVacancies().map { it.toDomain() }
    }

    override suspend fun getVacancyById(id: String): Vacancy {
        return api.getVacancyById(id).toDomain()
    }

    override suspend fun getEmployerVacancies(employerId: String): List<Vacancy> {
        return api.getEmployerVacancies(employerId).map { it.toDomain() }
    }

    override suspend fun createVacancy(input: VacancyInput): String {
        return api.createVacancy(input.toDto())
    }

    override suspend fun updateVacancy(id: String, input: VacancyInput) {
        return api.updateVacancy(id, input.toDto())
    }

    override suspend fun setVacancyActive(id: String, isActive: Boolean) {
        return api.setVacancyActive(id, isActive)
    }

    override suspend fun deleteVacancy(id: String) {
        return api.deleteVacancy(id)
    }
}
