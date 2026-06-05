package com.example.workflow.domain.model

data class EmployerStats(
    val totalVacancies: Int,
    val activeVacancies: Int,
    val totalApplications: Int,
    val pendingApplications: Int
)
