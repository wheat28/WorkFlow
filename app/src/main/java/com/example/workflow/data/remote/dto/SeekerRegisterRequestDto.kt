package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeekerRegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val city: String
)
