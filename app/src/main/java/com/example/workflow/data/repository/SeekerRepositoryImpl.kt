package com.example.workflow.data.repository

import com.example.workflow.data.remote.api.SeekerApi
import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.repository.SeekerRepository
import javax.inject.Inject

class SeekerRepositoryImpl @Inject constructor(
    private val api: SeekerApi
) : SeekerRepository {

    override suspend fun getById(seekerId: String): SeekerResponseDto {
        return api.getById(seekerId)
    }

    override suspend fun updateProfile(seekerId: String, request: SeekerUpdateRequestDto) {
        return api.updateProfile(seekerId, request)
    }
}
