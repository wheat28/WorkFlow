package com.example.workflow.domain.model

data class SeekerProfileInput(
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val city: String? = null,
    val about: String? = null
)
