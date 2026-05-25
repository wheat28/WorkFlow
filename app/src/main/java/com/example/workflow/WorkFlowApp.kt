package com.example.workflow

import android.app.Application
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.KtorClient
import com.example.workflow.data.remote.api.AuthApi
import com.example.workflow.domain.repository.AuthRepository
import com.example.workflow.domain.repository.AuthRepositoryImpl
import com.example.workflow.domain.usecase.LoginUseCase
import com.example.workflow.domain.usecase.LogoutUseCase
import com.example.workflow.domain.usecase.RegisterEmployerUseCase
import com.example.workflow.domain.usecase.RegisterSeekerUseCase
import io.ktor.client.HttpClient

class WorkFlowApp : Application() {

    val tokenDataStore: TokenDataStore by lazy { TokenDataStore(this) }

    private val httpClient: HttpClient by lazy { KtorClient.httpClient }
    private val api: AuthApi by lazy { AuthApi(httpClient) }

    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(api, tokenDataStore) }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    val registerSeekerUseCase: RegisterSeekerUseCase by lazy { RegisterSeekerUseCase(authRepository) }
    val registerEmployerUseCase: RegisterEmployerUseCase by lazy { RegisterEmployerUseCase(authRepository) }
    val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
}
