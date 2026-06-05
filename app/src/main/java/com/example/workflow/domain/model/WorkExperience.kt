package com.example.workflow.domain.model

data class WorkExperience(
    val id: String,
    val companyName: String,
    val position: String,
    val startDate: String,
    val endDate: String?,
    val description: String?
)