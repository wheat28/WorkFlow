package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResumeResponseDto(
    val id: String,
    val seekerId: String,
    val title: String,
    val position: String,
    val salaryExpected: Int?,
    val currency: String,
    val city: String?,
    val employmentType: String,
    val about: String?,
    val isActive: Boolean,
    val skills: List<String>,
    val workExperiences: List<WorkExperienceResponseDto>
)

@Serializable
data class WorkExperienceResponseDto(
    val id: String,
    val companyName: String,
    val position: String,
    val startDate: String,
    val endDate: String?,
    val description: String?
)
