package com.example.workflow.domain.model

data class VacancyInput(
    val title: String,
    val description: String,
    val employmentType: String,
    val experience: String,
    val city: String? = null,
    val salaryFrom: Int? = null,
    val salaryTo: Int? = null,
    val currency: String = "RUB"
)
