package com.example.workflow.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.usecase.favorite.GetFavoritesUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val vacancies: List<Vacancy>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var seekerId: String = ""

    init {
        viewModelScope.launch {
            seekerId = tokenDataStore.getUserId() ?: ""
            load()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { getFavoritesUseCase(seekerId) }
                .onSuccess { _uiState.value = UiState.Success(it.reversed()) }
            _isRefreshing.value = false
        }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getFavoritesUseCase(seekerId) }
                .onSuccess { _uiState.value = UiState.Success(it.reversed()) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun reload() {
        viewModelScope.launch {
            runCatching { getFavoritesUseCase(seekerId) }
                .onSuccess { _uiState.value = UiState.Success(it.reversed()) }
        }
    }

    fun remove(vacancyId: String) {
        val current = _uiState.value as? UiState.Success ?: return
        _uiState.value = UiState.Success(current.vacancies.filter { it.id != vacancyId })

        viewModelScope.launch {
            runCatching { removeFavoriteUseCase(vacancyId) }
                .onFailure { _uiState.value = UiState.Success(current.vacancies) }
        }
    }
}
