package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployerRegisterRequestDto(
    val email: String,
    val password: String,
    val companyName: String,
    val description: String,
    val website: String,
    val city: String,
    val industry: String,
    val phone: String
)
