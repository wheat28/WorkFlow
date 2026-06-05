package com.example.workflow.di

import android.content.Context
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.KtorClient
import com.example.workflow.data.remote.api.ApplicationApi
import com.example.workflow.data.remote.api.AuthApi
import com.example.workflow.data.remote.api.EmployerApi
import com.example.workflow.data.remote.api.FavoriteApi
import com.example.workflow.data.remote.api.ResumeApi
import com.example.workflow.data.remote.api.SeekerApi
import com.example.workflow.data.remote.api.VacancyApi
import com.example.workflow.data.repository.ApplicationRepositoryImpl
import com.example.workflow.data.repository.AuthRepositoryImpl
import com.example.workflow.data.repository.EmployerRepositoryImpl
import com.example.workflow.data.repository.FavoriteRepositoryImpl
import com.example.workflow.data.repository.ResumeRepositoryImpl
import com.example.workflow.data.repository.SeekerRepositoryImpl
import com.example.workflow.data.repository.VacancyRepositoryImpl
import com.example.workflow.domain.repository.ApplicationRepository
import com.example.workflow.domain.repository.AuthRepository
import com.example.workflow.domain.repository.EmployerRepository
import com.example.workflow.domain.repository.FavoriteRepository
import com.example.workflow.domain.repository.ResumeRepository
import com.example.workflow.domain.repository.SeekerRepository
import com.example.workflow.domain.repository.VacancyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Singleton
    @Binds
    fun bindVacancyRepository(
        impl: VacancyRepositoryImpl
    ): VacancyRepository

    @Singleton
    @Binds
    fun bindResumeRepository(
        impl: ResumeRepositoryImpl
    ): ResumeRepository

    @Singleton
    @Binds
    fun bindApplicationRepository(
        impl: ApplicationRepositoryImpl
    ): ApplicationRepository

    @Singleton
    @Binds
    fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Singleton
    @Binds
    fun bindEmployerRepository(
        impl: EmployerRepositoryImpl
    ): EmployerRepository

    @Singleton
    @Binds
    fun bindSeekerRepository(
        impl: SeekerRepositoryImpl
    ): SeekerRepository

    companion object {

        @Singleton
        @Provides
        fun provideTokenDataStore(
            @ApplicationContext context: Context
        ): TokenDataStore {
            return TokenDataStore(context)
        }

        @Singleton
        @Provides
        fun provideHttpClient(
            tokenDataStore: TokenDataStore
        ): HttpClient {
            return KtorClient.create(tokenDataStore)
        }

        @Singleton
        @Provides
        fun provideAuthApi(
            client: HttpClient
        ): AuthApi {
            return AuthApi(client)
        }

        @Singleton
        @Provides
        fun provideVacancyApi(
            client: HttpClient
        ): VacancyApi {
            return VacancyApi(client)
        }

        @Singleton
        @Provides
        fun provideResumeApi(
            client: HttpClient
        ): ResumeApi {
            return ResumeApi(client)
        }

        @Singleton
        @Provides
        fun provideApplicationApi(
            client: HttpClient
        ): ApplicationApi {
            return ApplicationApi(client)
        }

        @Singleton
        @Provides
        fun provideFavoriteApi(
            client: HttpClient
        ): FavoriteApi {
            return FavoriteApi(client)
        }

        @Singleton
        @Provides
        fun provideEmployerApi(
            client: HttpClient
        ): EmployerApi {
            return EmployerApi(client)
        }

        @Singleton
        @Provides
        fun provideSeekerApi(
            client: HttpClient
        ): SeekerApi {
            return SeekerApi(client)
        }
    }
}
