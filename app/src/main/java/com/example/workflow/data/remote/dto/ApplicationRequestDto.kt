package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApplicationRequestDto(
    val vacancyId: String,
    val resumeId: String,
    val coverLetter: String? = null
)
