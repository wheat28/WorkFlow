package com.example.workflow.domain.repository

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.api.AuthApi
import com.example.workflow.data.remote.dto.EmployerRegisterRequestDto
import com.example.workflow.data.remote.dto.LoginRequestDto
import com.example.workflow.data.remote.dto.SeekerRegisterRequestDto
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String) {
        val response = api.login(LoginRequestDto(email, password))
        tokenDataStore.saveToken(response.token, response.userType, response.userId)
    }

    override suspend fun registerSeeker(email: String, password: String, firstName: String, lastName: String, phone: String, city: String) {
        api.registerSeeker(SeekerRegisterRequestDto(email, password, firstName, lastName, phone, city))
        login(email, password)
    }

    override suspend fun registerEmployer(email: String, password: String, companyName: String, description: String, website: String, city: String, industry: String, phone: String) {
        api.registerEmployer(EmployerRegisterRequestDto(email, password, companyName, description, website, city, industry, phone))
        login(email, password)
    }

    override suspend fun logout() = tokenDataStore.clearToken()

    override suspend fun getToken(): String? = tokenDataStore.getToken()

    override fun getTokenFlow(): Flow<String?> = tokenDataStore.tokenFlow
}
