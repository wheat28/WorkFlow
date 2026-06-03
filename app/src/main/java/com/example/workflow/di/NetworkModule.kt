package com.example.workflow.di

import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.KtorClient
import com.example.workflow.data.remote.api.ApplicationApi
import com.example.workflow.data.remote.api.AuthApi
import com.example.workflow.data.remote.api.EmployerApi
import com.example.workflow.data.remote.api.FavoriteApi
import com.example.workflow.data.remote.api.ResumeApi
import com.example.workflow.data.remote.api.SeekerApi
import com.example.workflow.data.remote.api.VacancyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(tokenDataStore: TokenDataStore): HttpClient = KtorClient.create(tokenDataStore)

    @Provides
    @Singleton
    fun provideAuthApi(client: HttpClient) = AuthApi(client)

    @Provides
    @Singleton
    fun provideVacancyApi(client: HttpClient) = VacancyApi(client)

    @Provides
    @Singleton
    fun provideResumeApi(client: HttpClient) = ResumeApi(client)

    @Provides
    @Singleton
    fun provideApplicationApi(client: HttpClient) = ApplicationApi(client)

    @Provides
    @Singleton
    fun provideFavoriteApi(client: HttpClient) = FavoriteApi(client)

    @Provides
    @Singleton
    fun provideEmployerApi(client: HttpClient) = EmployerApi(client)

    @Provides
    @Singleton
    fun provideSeekerApi(client: HttpClient) = SeekerApi(client)
}
