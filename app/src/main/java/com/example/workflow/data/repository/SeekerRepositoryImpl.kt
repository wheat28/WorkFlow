package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.mapper.toDto
import com.example.workflow.data.remote.api.SeekerApi
import com.example.workflow.domain.model.Seeker
import com.example.workflow.domain.model.SeekerProfileInput
import com.example.workflow.domain.repository.SeekerRepository
import javax.inject.Inject

class SeekerRepositoryImpl @Inject constructor(
    private val api: SeekerApi
) : SeekerRepository {

    override suspend fun getById(seekerId: String): Seeker {
        return api.getById(seekerId).toDomain()
    }

    override suspend fun updateProfile(seekerId: String, input: SeekerProfileInput) {
        return api.updateProfile(seekerId, input.toDto())
    }
}
