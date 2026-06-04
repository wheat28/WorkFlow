package com.example.workflow.presentation.vacancy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.application.CheckAppliedUseCase
import com.example.workflow.domain.usecase.favorite.CheckFavoriteUseCase
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VacancyDetailViewModel @Inject constructor(
    private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkAppliedUseCase: CheckAppliedUseCase,
    private val deleteVacancyUseCase: DeleteVacancyUseCase,
    private val tokenDataStore: TokenDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vacancyId: String = checkNotNull(savedStateHandle["id"])

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val vacancy: Vacancy,
            val isFavorite: Boolean = false,
            val isApplied: Boolean = false
        ) : UiState()
        data class Error(val message: String) : UiState()
        object Deleted : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getVacancyByIdUseCase(vacancyId) }
                .onSuccess { vacancy ->
                    val isSeeker = tokenDataStore.getUserType() == "SEEKER"
                    val favDeferred = async {
                        if (isSeeker) checkFavoriteUseCase.runCatching { invoke(vacancyId) }.getOrDefault(false)
                        else false
                    }
                    val appliedDeferred = async {
                        if (isSeeker) checkAppliedUseCase.runCatching { invoke(vacancyId) }.getOrDefault(false)
                        else false
                    }
                    _uiState.value = UiState.Success(
                        vacancy = vacancy,
                        isFavorite = favDeferred.await(),
                        isApplied = appliedDeferred.await()
                    )
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value as? UiState.Success ?: return
        viewModelScope.launch {
            if (current.isFavorite) {
                removeFavoriteUseCase.runCatching { invoke(vacancyId) }
                _uiState.value = current.copy(isFavorite = false)
            } else {
                addFavoriteUseCase.runCatching { invoke(vacancyId) }
                _uiState.value = current.copy(isFavorite = true)
            }
        }
    }

    fun markAsApplied() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isApplied = true)
    }

    fun deleteVacancy() {
        viewModelScope.launch {
            runCatching { deleteVacancyUseCase(vacancyId) }
                .onSuccess { _uiState.value = UiState.Deleted }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка удаления") }
        }
    }
}
