package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.ResumeRequestDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.data.remote.dto.WorkExperienceResponseDto
import com.example.workflow.domain.model.Resume
import com.example.workflow.domain.model.ResumeInput
import com.example.workflow.domain.model.WorkExperience

fun ResumeResponseDto.toDomain() = Resume(
    id = id,
    seekerId = seekerId,
    title = title,
    position = position,
    salaryExpected = salaryExpected,
    currency = currency,
    city = city,
    employmentType = employmentType,
    about = about,
    isActive = isActive,
    skills = skills,
    workExperiences = workExperiences.map { it.toDomain() }
)

fun WorkExperienceResponseDto.toDomain() = WorkExperience(
    id = id,
    companyName = companyName,
    position = position,
    startDate = startDate,
    endDate = endDate,
    description = description
)

fun ResumeInput.toDto() = ResumeRequestDto(
    title = title,
    position = position,
    employmentType = employmentType,
    salaryExpected = salaryExpected,
    currency = currency,
    city = city,
    about = about
)
