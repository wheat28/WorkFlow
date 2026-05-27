package com.example.workflow.presentation.vacancy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.application.CheckAppliedUseCase
import com.example.workflow.domain.usecase.favorite.CheckFavoriteUseCase
import com.example.workflow.domain.usecase.vacancy.DeleteVacancyUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacancyByIdUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VacancyDetailViewModel(
    private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase?,
    private val addFavoriteUseCase: AddFavoriteUseCase?,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase?,
    private val checkAppliedUseCase: CheckAppliedUseCase?,
    private val deleteVacancyUseCase: DeleteVacancyUseCase?,
    private val vacancyId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val vacancy: VacancyResponseDto,
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
                    val favDeferred = async { checkFavoriteUseCase?.runCatching { invoke(vacancyId) }?.getOrDefault(false) ?: false }
                    val appliedDeferred = async { checkAppliedUseCase?.runCatching { invoke(vacancyId) }?.getOrDefault(false) ?: false }
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
                removeFavoriteUseCase?.runCatching { invoke(vacancyId) }
                _uiState.value = current.copy(isFavorite = false)
            } else {
                addFavoriteUseCase?.runCatching { invoke(vacancyId) }
                _uiState.value = current.copy(isFavorite = true)
            }
        }
    }

    fun markAsApplied() {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = current.copy(isApplied = true)
    }

    fun deleteVacancy() {
        val useCase = deleteVacancyUseCase ?: return
        viewModelScope.launch {
            runCatching { useCase(vacancyId) }
                .onSuccess { _uiState.value = UiState.Deleted }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка удаления") }
        }
    }

    class Factory(
        private val getVacancyByIdUseCase: GetVacancyByIdUseCase,
        private val vacancyId: String,
        private val checkFavoriteUseCase: CheckFavoriteUseCase? = null,
        private val addFavoriteUseCase: AddFavoriteUseCase? = null,
        private val removeFavoriteUseCase: RemoveFavoriteUseCase? = null,
        private val checkAppliedUseCase: CheckAppliedUseCase? = null,
        private val deleteVacancyUseCase: DeleteVacancyUseCase? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            VacancyDetailViewModel(
                getVacancyByIdUseCase,
                checkFavoriteUseCase, addFavoriteUseCase, removeFavoriteUseCase,
                checkAppliedUseCase, deleteVacancyUseCase, vacancyId
            ) as T
    }
}
