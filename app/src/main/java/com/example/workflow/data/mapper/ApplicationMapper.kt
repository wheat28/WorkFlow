package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.model.Application

fun ApplicationResponseDto.toDomain() = Application(
    id = id,
    seekerId = seekerId,
    seekerFirstName = seekerFirstName,
    seekerLastName = seekerLastName,
    vacancyId = vacancyId,
    vacancyTitle = vacancyTitle,
    resumeId = resumeId,
    status = status,
    coverLetter = coverLetter,
    createdAt = createdAt
)
