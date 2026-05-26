package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VacancyRequestDto(
    val title: String,
    val description: String,
    val employmentType: String,
    val experience: String,
    val city: String? = null,
    val salaryFrom: Int? = null,
    val salaryTo: Int? = null,
    val currency: String = "RUB"
)
