package com.example.workflow.data.repository

import com.example.workflow.data.remote.api.ResumeApi
import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.repository.ResumeRepository
import javax.inject.Inject

class ResumeRepositoryImpl @Inject constructor(
    private val api: ResumeApi
) : ResumeRepository {

    override suspend fun getMyResumes(seekerId: String): List<ResumeResponseDto> {
        return api.getMyResumes(seekerId)
    }

    override suspend fun getResumeById(id: String): ResumeResponseDto {
        return api.getResumeById(id)
    }

    override suspend fun createResume(request: ResumeRequestDto): String {
        return api.createResume(request)
    }

    override suspend fun updateResume(id: String, request: ResumeRequestDto) {
        return api.updateResume(id, request)
    }

    override suspend fun setResumeActive(id: String, isActive: Boolean) {
        return api.setResumeActive(id, isActive)
    }

    override suspend fun deleteResume(id: String) {
        return api.deleteResume(id)
    }
}
