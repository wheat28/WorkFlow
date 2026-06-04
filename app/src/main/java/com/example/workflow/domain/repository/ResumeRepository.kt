package com.example.workflow.domain.repository

import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.model.ResumeInput

interface ResumeRepository {
    suspend fun getMyResumes(seekerId: String): List<Resume>
    suspend fun getResumeById(id: String): Resume
    suspend fun createResume(input: ResumeInput): String
    suspend fun updateResume(id: String, input: ResumeInput)
    suspend fun setResumeActive(id: String, isActive: Boolean)
    suspend fun deleteResume(id: String)
}
