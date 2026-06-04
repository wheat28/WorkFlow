package com.example.workflow.domain.model

data class Seeker(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val city: String? = null,
    val about: String? = null
)
