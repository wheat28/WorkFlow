package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployerStatsDto(
    val totalVacancies: Int,
    val activeVacancies: Int,
    val totalApplications: Int,
    val pendingApplications: Int
)
