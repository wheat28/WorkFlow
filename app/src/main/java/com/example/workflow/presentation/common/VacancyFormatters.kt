package com.example.workflow.presentation.common

fun employmentLabel(type: String) = when (type) {
    "FULL_TIME"   -> "Полная занятость"
    "PART_TIME"   -> "Частичная занятость"
    "REMOTE"      -> "Удалённо"
    "INTERNSHIP"  -> "Стажировка"
    else          -> type
}

fun experienceLabel(value: String) = when (value) {
    "NO_EXPERIENCE" -> "Без опыта"
    "1_3"           -> "1–3 года"
    "3_6"           -> "3–6 лет"
    "6_PLUS"        -> "Более 6 лет"
    else            -> value
}
