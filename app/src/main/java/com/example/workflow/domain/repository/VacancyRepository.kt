package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto

interface VacancyRepository {
    suspend fun getAllVacancies(): List<VacancyResponseDto>
    suspend fun getVacancyById(id: String): VacancyResponseDto
    suspend fun getEmployerVacancies(employerId: String): List<VacancyResponseDto>
    suspend fun createVacancy(request: VacancyRequestDto): String
    suspend fun updateVacancy(id: String, request: VacancyRequestDto)
    suspend fun deleteVacancy(id: String)
}
