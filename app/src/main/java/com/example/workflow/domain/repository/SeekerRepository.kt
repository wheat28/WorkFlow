package com.example.workflow.domain.repository

import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto

interface SeekerRepository {
    suspend fun getById(seekerId: String): SeekerResponseDto
    suspend fun updateProfile(seekerId: String, request: SeekerUpdateRequestDto)
}
