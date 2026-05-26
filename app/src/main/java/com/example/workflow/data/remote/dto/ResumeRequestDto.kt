package com.example.workflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResumeRequestDto(
    val title: String,
    val position: String,
    val employmentType: String,
    val salaryExpected: Int? = null,
    val currency: String = "RUB",
    val city: String? = null,
    val about: String? = null
)
