package com.example.workflow.di

import com.example.workflow.data.repository.ApplicationRepositoryImpl
import com.example.workflow.data.repository.EmployerRepositoryImpl
import com.example.workflow.data.repository.FavoriteRepositoryImpl
import com.example.workflow.data.repository.ResumeRepositoryImpl
import com.example.workflow.data.repository.SeekerRepositoryImpl
import com.example.workflow.data.repository.VacancyRepositoryImpl
import com.example.workflow.domain.repository.ApplicationRepository
import com.example.workflow.data.repository.AuthRepositoryImpl
import com.example.workflow.domain.repository.AuthRepository
import com.example.workflow.domain.repository.EmployerRepository
import com.example.workflow.domain.repository.FavoriteRepository
import com.example.workflow.domain.repository.ResumeRepository
import com.example.workflow.domain.repository.SeekerRepository
import com.example.workflow.domain.repository.VacancyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindVacancyRepository(impl: VacancyRepositoryImpl): VacancyRepository

    @Binds @Singleton
    abstract fun bindResumeRepository(impl: ResumeRepositoryImpl): ResumeRepository

    @Binds @Singleton
    abstract fun bindApplicationRepository(impl: ApplicationRepositoryImpl): ApplicationRepository

    @Binds @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds @Singleton
    abstract fun bindEmployerRepository(impl: EmployerRepositoryImpl): EmployerRepository

    @Binds @Singleton
    abstract fun bindSeekerRepository(impl: SeekerRepositoryImpl): SeekerRepository
}
