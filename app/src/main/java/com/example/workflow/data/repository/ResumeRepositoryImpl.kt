package com.example.workflow.data.repository

import com.example.workflow.data.mapper.toDomain
import com.example.workflow.data.mapper.toDto
import com.example.workflow.data.remote.api.ResumeApi
import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.model.ResumeInput
import com.example.workflow.domain.repository.ResumeRepository
import javax.inject.Inject

class ResumeRepositoryImpl @Inject constructor(
    private val api: ResumeApi
) : ResumeRepository {

    override suspend fun getMyResumes(seekerId: String): List<Resume> {
        return api.getMyResumes(seekerId).map { it.toDomain() }
    }

    override suspend fun getResumeById(id: String): Resume {
        return api.getResumeById(id).toDomain()
    }

    override suspend fun createResume(input: ResumeInput): String {
        return api.createResume(input.toDto())
    }

    override suspend fun updateResume(id: String, input: ResumeInput) {
        return api.updateResume(id, input.toDto())
    }

    override suspend fun setResumeActive(id: String, isActive: Boolean) {
        return api.setResumeActive(id, isActive)
    }

    override suspend fun deleteResume(id: String) {
        return api.deleteResume(id)
    }
}
