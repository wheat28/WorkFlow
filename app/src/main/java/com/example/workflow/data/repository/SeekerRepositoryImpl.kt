package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.SeekerApi
import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.repository.SeekerRepository

class SeekerRepositoryImpl(
    private val api: SeekerApi,
    private val tokenDataStore: TokenDataStore
) : SeekerRepository {

    override suspend fun getById(seekerId: String): SeekerResponseDto {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getById(token, seekerId)
    }

    override suspend fun updateProfile(seekerId: String, request: SeekerUpdateRequestDto) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.updateProfile(token, seekerId, request)
    }
}
