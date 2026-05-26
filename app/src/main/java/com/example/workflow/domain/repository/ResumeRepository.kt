package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto

interface ResumeRepository {
    suspend fun getMyResumes(seekerId: String): List<ResumeResponseDto>
    suspend fun getResumeById(id: String): ResumeResponseDto
    suspend fun createResume(request: ResumeRequestDto): String
    suspend fun updateResume(id: String, request: ResumeRequestDto)
}
