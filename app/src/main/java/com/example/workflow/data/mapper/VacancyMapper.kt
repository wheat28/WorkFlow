package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.VacancyRequestDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.model.VacancyInput

fun VacancyResponseDto.toDomain() = Vacancy(
    id = id,
    employerId = employerId,
    companyName = companyName,
    categoryId = categoryId,
    title = title,
    description = description,
    salaryFrom = salaryFrom,
    salaryTo = salaryTo,
    currency = currency,
    city = city,
    employmentType = employmentType,
    experience = experience,
    isActive = isActive,
    skills = skills,
    applicationCount = applicationCount
)

fun VacancyInput.toDto() = VacancyRequestDto(
    title = title,
    description = description,
    employmentType = employmentType,
    experience = experience,
    city = city,
    salaryFrom = salaryFrom,
    salaryTo = salaryTo,
    currency = currency
)
