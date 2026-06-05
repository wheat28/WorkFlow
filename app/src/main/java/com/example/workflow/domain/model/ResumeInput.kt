package com.example.workflow.domain.model

data class ResumeInput(
    val title: String,
    val position: String,
    val employmentType: String,
    val salaryExpected: Int? = null,
    val currency: String = "RUB",
    val city: String? = null,
    val about: String? = null
)
