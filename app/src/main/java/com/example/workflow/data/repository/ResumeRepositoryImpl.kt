package com.example.workflow.data.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.ResumeApi
import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.repository.ResumeRepository

class ResumeRepositoryImpl(
    private val api: ResumeApi,
    private val tokenDataStore: TokenDataStore
) : ResumeRepository {

    override suspend fun getMyResumes(seekerId: String): List<ResumeResponseDto> {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getMyResumes(token, seekerId)
    }

    override suspend fun getResumeById(id: String): ResumeResponseDto {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.getResumeById(token, id)
    }

    override suspend fun createResume(request: ResumeRequestDto): String {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        return api.createResume(token, request)
    }

    override suspend fun updateResume(id: String, request: ResumeRequestDto) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.updateResume(token, id, request)
    }

    override suspend fun setResumeActive(id: String, isActive: Boolean) {
        val token = tokenDataStore.getToken() ?: throw Exception("Не авторизован")
        api.setResumeActive(token, id, isActive)
    }
}
