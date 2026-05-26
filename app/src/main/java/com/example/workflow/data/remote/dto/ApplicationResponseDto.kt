package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationResponseDto(
    val id: String,
    val seekerId: String,
    val seekerFirstName: String,
    val seekerLastName: String,
    val vacancyId: String,
    val vacancyTitle: String,
    val resumeId: String,
    val status: String,
    val coverLetter: String?,
    val createdAt: String
)
