package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val token: String,
    val userType: String
)
