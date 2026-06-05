package com.example.workflow.domain.repository

import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.model.VacancyInput

interface VacancyRepository {
    suspend fun getAllVacancies(): List<Vacancy>
    suspend fun getVacancyById(id: String): Vacancy
    suspend fun getEmployerVacancies(employerId: String): List<Vacancy>
    suspend fun createVacancy(input: VacancyInput): String
    suspend fun updateVacancy(id: String, input: VacancyInput)
    suspend fun setVacancyActive(id: String, isActive: Boolean)
    suspend fun deleteVacancy(id: String)
}
