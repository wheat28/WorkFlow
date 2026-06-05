package com.example.workflow.domain.repository

import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.model.EmployerProfileInput
import com.example.workflow.domain.model.EmployerStats

interface EmployerRepository {
    suspend fun getById(employerId: String): Employer
    suspend fun updateProfile(employerId: String, input: EmployerProfileInput)
    suspend fun getStats(employerId: String): EmployerStats
}
