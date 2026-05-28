package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeekerResponseDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val city: String? = null,
    val about: String? = null
)
