package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.AuthResponseDto
import com.example.workflow.domain.model.AuthResult

fun AuthResponseDto.toDomain() = AuthResult(
    token = token,
    userType = userType,
    userId = userId,
    displayName = displayName
)
