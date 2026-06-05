package com.example.workflow.domain.model

data class Employer(
    val id: String,
    val email: String,
    val companyName: String,
    val description: String? = null,
    val website: String? = null,
    val logoUrl: String? = null,
    val city: String? = null,
    val industry: String? = null,
    val phone: String? = null
)
