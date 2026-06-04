package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.mapper.toDto
import com.example.workflow.data.remote.api.EmployerApi
import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.model.EmployerProfileInput
import com.example.workflow.domain.model.EmployerStats
import com.example.workflow.domain.repository.EmployerRepository
import javax.inject.Inject

class EmployerRepositoryImpl @Inject constructor(
    private val api: EmployerApi
) : EmployerRepository {

    override suspend fun getById(employerId: String): Employer {
        return api.getById(employerId).toDomain()
    }

    override suspend fun updateProfile(employerId: String, input: EmployerProfileInput) {
        return api.updateProfile(employerId, input.toDto())
    }

    override suspend fun getStats(employerId: String): EmployerStats {
        return api.getStats(employerId).toDomain()
    }
}
