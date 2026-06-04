package com.example.workflow.data.mapper

import com.example.workflow.data.remote.dto.SeekerResponseDto
import com.example.workflow.data.remote.dto.SeekerUpdateRequestDto
import com.example.workflow.domain.model.Seeker
import com.example.workflow.domain.model.SeekerProfileInput

fun SeekerResponseDto.toDomain() = Seeker(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    city = city,
    about = about
)

fun SeekerProfileInput.toDto() = SeekerUpdateRequestDto(
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    city = city,
    about = about
)
