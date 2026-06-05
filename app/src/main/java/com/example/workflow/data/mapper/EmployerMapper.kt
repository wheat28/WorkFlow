package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.EmployerStatsDto
import com.example.workflow.data.remote.dto.EmployerUpdateRequestDto
import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.model.EmployerProfileInput
import com.example.workflow.domain.model.EmployerStats

fun EmployerResponseDto.toDomain() = Employer(
    id = id,
    email = email,
    companyName = companyName,
    description = description,
    website = website,
    logoUrl = logoUrl,
    city = city,
    industry = industry,
    phone = phone
)

fun EmployerStatsDto.toDomain() = EmployerStats(
    totalVacancies = totalVacancies,
    activeVacancies = activeVacancies,
    totalApplications = totalApplications,
    pendingApplications = pendingApplications
)

fun EmployerProfileInput.toDto() = EmployerUpdateRequestDto(
    companyName = companyName,
    description = description,
    website = website,
    city = city,
    industry = industry,
    phone = phone
)
