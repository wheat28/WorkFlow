package com.example.workflow.presentation.screens.employer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.domain.model.Employer
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.vacancy.GetEmployerVacanciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerPublicProfileViewModel @Inject constructor(
    private val getEmployerByIdUseCase: GetEmployerByIdUseCase,
    private val getEmployerVacanciesUseCase: GetEmployerVacanciesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val employerId: String = checkNotNull(savedStateHandle["employerId"])

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val employer: Employer,
            val vacancies: List<Vacancy>
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init { load() }

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
}
