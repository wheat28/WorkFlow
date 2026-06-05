package com.example.workflow.domain.model

data class EmployerProfileInput(
    val companyName: String,
    val description: String? = null,
    val website: String? = null,
    val city: String? = null,
    val industry: String? = null,
    val phone: String? = null
)
