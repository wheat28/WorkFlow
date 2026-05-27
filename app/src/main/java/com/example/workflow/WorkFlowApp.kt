package com.example.workflow

import android.app.Application
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
import com.example.workflow.data.repository.EmployerRepositoryImpl
import com.example.workflow.data.repository.FavoriteRepositoryImpl
import com.example.workflow.data.repository.ResumeRepositoryImpl
import com.example.workflow.data.repository.SeekerRepositoryImpl
import com.example.workflow.data.repository.VacancyRepositoryImpl
import com.example.workflow.domain.repository.ApplicationRepository
import com.example.workflow.domain.repository.AuthRepository
import com.example.workflow.domain.repository.AuthRepositoryImpl
import com.example.workflow.domain.repository.EmployerRepository
import com.example.workflow.domain.repository.FavoriteRepository
import com.example.workflow.domain.repository.ResumeRepository
import com.example.workflow.domain.repository.SeekerRepository
import com.example.workflow.domain.repository.VacancyRepository
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.application.ApplyForVacancyUseCase
import com.example.workflow.domain.usecase.application.CheckAppliedUseCase
import com.example.workflow.domain.usecase.favorite.CheckFavoriteUseCase
import com.example.workflow.domain.usecase.resume.CreateResumeUseCase
import com.example.workflow.domain.usecase.vacancy.CreateVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.UpdateVacancyUseCase
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.employer.GetEmployerStatsUseCase
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import com.example.workflow.domain.usecase.employer.UpdateEmployerUseCase
import com.example.workflow.domain.usecase.favorite.GetFavoritesUseCase
import com.example.workflow.domain.usecase.application.CancelApplicationUseCase
import com.example.workflow.domain.usecase.application.GetMyApplicationsUseCase
import com.example.workflow.domain.usecase.application.GetVacancyApplicationsUseCase
import com.example.workflow.domain.usecase.application.UpdateApplicationStatusUseCase
import com.example.workflow.domain.usecase.resume.GetMyResumesUseCase
import com.example.workflow.domain.usecase.resume.GetResumeByIdUseCase
import com.example.workflow.domain.usecase.seeker.GetSeekerByIdUseCase
import com.example.workflow.domain.usecase.seeker.UpdateSeekerUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacanciesUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.auth.LoginUseCase
import com.example.workflow.domain.usecase.auth.LogoutUseCase
import com.example.workflow.domain.usecase.auth.RegisterEmployerUseCase
import com.example.workflow.domain.usecase.auth.RegisterSeekerUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.workflow.domain.usecase.resume.SetResumeActiveUseCase
import com.example.workflow.domain.usecase.vacancy.SetVacancyActiveUseCase
import com.example.workflow.domain.usecase.resume.UpdateResumeUseCase
import io.ktor.client.HttpClient

class WorkFlowApp : Application() {

    val tokenDataStore: TokenDataStore by lazy { TokenDataStore(this) }

    private val httpClient: HttpClient by lazy { KtorClient.httpClient }
    private val authApi: AuthApi by lazy { AuthApi(httpClient) }
    private val vacancyApi: VacancyApi by lazy { VacancyApi(httpClient) }
    private val resumeApi: ResumeApi by lazy { ResumeApi(httpClient) }
    private val applicationApi: ApplicationApi by lazy { ApplicationApi(httpClient) }
    private val favoriteApi: FavoriteApi by lazy { FavoriteApi(httpClient) }
    private val employerApi: EmployerApi by lazy { EmployerApi(httpClient) }
    private val seekerApi: SeekerApi by lazy { SeekerApi(httpClient) }

    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authApi, tokenDataStore) }
    val vacancyRepository: VacancyRepository by lazy { VacancyRepositoryImpl(vacancyApi, tokenDataStore) }
    val resumeRepository: ResumeRepository by lazy { ResumeRepositoryImpl(resumeApi, tokenDataStore) }
    val applicationRepository: ApplicationRepository by lazy { ApplicationRepositoryImpl(applicationApi, tokenDataStore) }
    val favoriteRepository: FavoriteRepository by lazy { FavoriteRepositoryImpl(favoriteApi, tokenDataStore) }
    val employerRepository: EmployerRepository by lazy { EmployerRepositoryImpl(employerApi, tokenDataStore) }
    val seekerRepository: SeekerRepository by lazy { SeekerRepositoryImpl(seekerApi, tokenDataStore) }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    val registerSeekerUseCase: RegisterSeekerUseCase by lazy { RegisterSeekerUseCase(authRepository) }
    val registerEmployerUseCase: RegisterEmployerUseCase by lazy { RegisterEmployerUseCase(authRepository) }
    val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
    val getVacanciesUseCase: GetVacanciesUseCase by lazy { GetVacanciesUseCase(vacancyRepository) }
    val getVacancyByIdUseCase: GetVacancyByIdUseCase by lazy { GetVacancyByIdUseCase(vacancyRepository) }
    val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase by lazy { GetEmployerVacanciesUseCase(vacancyRepository) }
    val createVacancyUseCase: CreateVacancyUseCase by lazy { CreateVacancyUseCase(vacancyRepository) }
    val updateVacancyUseCase: UpdateVacancyUseCase by lazy { UpdateVacancyUseCase(vacancyRepository) }
    val deleteVacancyUseCase: DeleteVacancyUseCase by lazy { DeleteVacancyUseCase(vacancyRepository) }
    val setVacancyActiveUseCase: SetVacancyActiveUseCase by lazy { SetVacancyActiveUseCase(vacancyRepository) }
    val getEmployerStatsUseCase: GetEmployerStatsUseCase by lazy { GetEmployerStatsUseCase(employerRepository) }
    val getEmployerByIdUseCase: GetEmployerByIdUseCase by lazy { GetEmployerByIdUseCase(employerRepository) }
    val updateEmployerUseCase: UpdateEmployerUseCase by lazy { UpdateEmployerUseCase(employerRepository) }
    val getSeekerByIdUseCase: GetSeekerByIdUseCase by lazy { GetSeekerByIdUseCase(seekerRepository) }
    val updateSeekerUseCase: UpdateSeekerUseCase by lazy { UpdateSeekerUseCase(seekerRepository) }
    val getMyResumesUseCase: GetMyResumesUseCase by lazy { GetMyResumesUseCase(resumeRepository) }
    val getResumeByIdUseCase: GetResumeByIdUseCase by lazy { GetResumeByIdUseCase(resumeRepository) }
    val createResumeUseCase: CreateResumeUseCase by lazy { CreateResumeUseCase(resumeRepository) }
    val updateResumeUseCase: UpdateResumeUseCase by lazy { UpdateResumeUseCase(resumeRepository) }
    val setResumeActiveUseCase: SetResumeActiveUseCase by lazy { SetResumeActiveUseCase(resumeRepository) }
    val applyForVacancyUseCase: ApplyForVacancyUseCase by lazy { ApplyForVacancyUseCase(applicationRepository) }
    val getMyApplicationsUseCase: GetMyApplicationsUseCase by lazy { GetMyApplicationsUseCase(applicationRepository) }
    val cancelApplicationUseCase: CancelApplicationUseCase by lazy { CancelApplicationUseCase(applicationRepository) }
    val addFavoriteUseCase: AddFavoriteUseCase by lazy { AddFavoriteUseCase(favoriteRepository) }
    val removeFavoriteUseCase: RemoveFavoriteUseCase by lazy { RemoveFavoriteUseCase(favoriteRepository) }
    val getFavoritesUseCase: GetFavoritesUseCase by lazy { GetFavoritesUseCase(favoriteRepository) }
    val checkFavoriteUseCase: CheckFavoriteUseCase by lazy { CheckFavoriteUseCase(favoriteRepository) }
    val checkAppliedUseCase: CheckAppliedUseCase by lazy { CheckAppliedUseCase(applicationRepository) }
    val getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase by lazy { GetVacancyApplicationsUseCase(applicationRepository) }
    val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase by lazy { UpdateApplicationStatusUseCase(applicationRepository) }
}
