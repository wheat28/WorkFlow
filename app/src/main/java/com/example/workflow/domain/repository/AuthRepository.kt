package com.example.workflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String)
    suspend fun registerSeeker(email: String, password: String, firstName: String, lastName: String, phone: String, city: String)
    suspend fun registerEmployer(email: String, password: String, companyName: String, description: String, website: String, city: String, industry: String, phone: String)
    suspend fun logout()
    suspend fun getToken(): String?
    fun getTokenFlow(): Flow<String?>
}
