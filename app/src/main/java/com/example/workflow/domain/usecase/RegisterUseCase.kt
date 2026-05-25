package com.example.workflow.domain.usecase

import com.example.workflow.domain.repository.AuthRepository

class RegisterSeekerUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, firstName: String, lastName: String, phone: String, city: String) =
        repository.registerSeeker(email, password, firstName, lastName, phone, city)
}

class RegisterEmployerUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, companyName: String, description: String, website: String, city: String, industry: String, phone: String) =
        repository.registerEmployer(email, password, companyName, description, website, city, industry, phone)
}
