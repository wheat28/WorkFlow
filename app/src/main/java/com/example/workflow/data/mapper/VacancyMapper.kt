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
    employmentType = mapEmploymentType(employmentType),
    experience = mapExperience(experience),
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

private fun mapEmploymentType(value: String) = when (value) {
    "FULL_TIME"  -> "Полная занятость"
    "PART_TIME"  -> "Частичная занятость"
    "REMOTE"     -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else         -> value
}

private fun mapExperience(value: String) = when (value) {
    "NO_EXPERIENCE" -> "Без опыта"
    "1_3"           -> "1-3 года"
    "3_6"           -> "3-6 лет"
    "6_PLUS"        -> "Более 6 лет"
    else            -> value
}
