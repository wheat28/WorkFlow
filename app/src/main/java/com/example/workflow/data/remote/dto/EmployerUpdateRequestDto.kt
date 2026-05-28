package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployerUpdateRequestDto(
    val companyName: String,
    val description: String? = null,
    val website: String? = null,
    val city: String? = null,
    val industry: String? = null,
    val phone: String? = null
)
