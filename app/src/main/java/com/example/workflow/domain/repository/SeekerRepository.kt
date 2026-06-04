package com.example.workflow.domain.repository

import com.example.workflow.domain.model.Seeker
import com.example.workflow.domain.model.SeekerProfileInput

interface SeekerRepository {
    suspend fun getById(seekerId: String): Seeker
    suspend fun updateProfile(seekerId: String, input: SeekerProfileInput)
}
