package com.example.workflow.presentation.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployerPublicProfileViewModel(
    private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
    private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    private val employerId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val employer: EmployerResponseDto,
            val vacancies: List<VacancyResponseDto>
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching {
                val employerDeferred = async { getEmployerByIdUseCase(employerId) }
                val vacanciesDeferred = async { getEmployerVacanciesUseCase(employerId) }
                Pair(employerDeferred.await(), vacanciesDeferred.await())
            }.onSuccess { (employer, vacancies) ->
                _uiState.value = UiState.Success(
                    employer = employer,
                    vacancies = vacancies.filter { it.isActive }
                )
            }.onFailure {
                _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки")
            }
        }
    }

    class Factory(
        private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
        private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
        private val employerId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            EmployerPublicProfileViewModel(getEmployerByIdUseCase, getEmployerVacanciesUseCase, employerId) as T
    }
}
