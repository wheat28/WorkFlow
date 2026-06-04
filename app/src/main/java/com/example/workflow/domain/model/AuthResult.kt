package com.example.workflow.domain.model

data class AuthResult(
    val token: String,
    val userType: String,
    val userId: String,
    val displayName: String
)
