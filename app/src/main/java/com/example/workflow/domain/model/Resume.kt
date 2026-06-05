package com.example.workflow.domain.model

data class Resume(
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
    val workExperiences: List<WorkExperience>
)

data class WorkExperience(
    val id: String,
    val companyName: String,
    val position: String,
    val startDate: String,
    val endDate: String?,
    val description: String?
)
