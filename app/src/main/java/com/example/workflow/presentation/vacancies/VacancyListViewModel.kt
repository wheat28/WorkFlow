package com.example.workflow.presentation.vacancies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.favorite.GetFavoritesUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacanciesUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class VacancyListViewModel(
    private val getVacanciesUseCase: GetVacanciesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase?,
    private val addFavoriteUseCase: AddFavoriteUseCase?,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase?,
    private val seekerId: String?
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val vacancies: List<VacancyResponseDto>, val favoriteIds: Set<String>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedCity = MutableStateFlow("")
    val selectedEmploymentType = MutableStateFlow("")
    val salaryFrom = MutableStateFlow("")
    val salaryTo = MutableStateFlow("")

    private var allVacancies: List<VacancyResponseDto> = emptyList()
    private var allFavoriteIds: Set<String> = emptySet()

    init {
        loadVacancies()
        viewModelScope.launch {
            searchQuery.debounce(300).collect { applyFilters() }
        }
    }

    fun loadVacancies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getVacanciesUseCase() }
                .onSuccess {
                    allVacancies = it
                    loadFavorites()
                }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            runCatching { getVacanciesUseCase() }
                .onSuccess { vacancies ->
                    allVacancies = vacancies
                    if (getFavoritesUseCase != null && seekerId != null) {
                        runCatching { getFavoritesUseCase.invoke(seekerId) }
                            .onSuccess { favs -> allFavoriteIds = favs.map { it.id }.toSet() }
                    }
                    applyFilters()
                }
            _isRefreshing.value = false
        }
    }

    fun loadFavorites() {
        if (getFavoritesUseCase == null || seekerId == null) {
            applyFilters()
            return
        }
        viewModelScope.launch {
            runCatching { getFavoritesUseCase.invoke(seekerId) }
                .onSuccess { favorites ->
                    allFavoriteIds = favorites.map { it.id }.toSet()
                    applyFilters()
                }
                .onFailure { applyFilters() }
        }
    }

    fun toggleFavorite(vacancyId: String) {
        val wasFavorite = vacancyId in allFavoriteIds
        allFavoriteIds = if (wasFavorite) allFavoriteIds - vacancyId else allFavoriteIds + vacancyId
        applyFilters()

        viewModelScope.launch {
            val result = if (wasFavorite) {
                runCatching { removeFavoriteUseCase?.invoke(vacancyId) }
            } else {
                runCatching { addFavoriteUseCase?.invoke(vacancyId) }
            }
            result.onFailure {
                allFavoriteIds = if (wasFavorite) allFavoriteIds + vacancyId else allFavoriteIds - vacancyId
                applyFilters()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun clearFilters() {
        selectedCity.value = ""
        selectedEmploymentType.value = ""
        salaryFrom.value = ""
        salaryTo.value = ""
        applyFilters()
    }

    fun onCitySelected(city: String) {
        selectedCity.value = city
        applyFilters()
    }

    fun onEmploymentTypeSelected(type: String) {
        selectedEmploymentType.value = type
        applyFilters()
    }

    fun onSalaryFromChanged(value: String) {
        salaryFrom.value = value
        applyFilters()
    }

    fun onSalaryToChanged(value: String) {
        salaryTo.value = value
        applyFilters()
    }

    private fun applyFilters() {
        val filterFrom = salaryFrom.value.toIntOrNull()
        val filterTo = salaryTo.value.toIntOrNull()
        val filtered = allVacancies.filter { vacancy ->
            (searchQuery.value.isBlank() ||
                vacancy.title.contains(searchQuery.value, ignoreCase = true) ||
                vacancy.companyName.contains(searchQuery.value, ignoreCase = true)) &&
            (selectedCity.value.isBlank() ||
                vacancy.city.orEmpty().equals(selectedCity.value, ignoreCase = true)) &&
            (selectedEmploymentType.value.isBlank() ||
                vacancy.employmentType.equals(selectedEmploymentType.value, ignoreCase = true)) &&
            (filterFrom == null || (vacancy.salaryFrom != null && vacancy.salaryFrom >= filterFrom) ||
                (vacancy.salaryFrom == null && vacancy.salaryTo != null && vacancy.salaryTo >= filterFrom)) &&
            (filterTo == null || (vacancy.salaryTo != null && vacancy.salaryTo <= filterTo) ||
                (vacancy.salaryTo == null && vacancy.salaryFrom != null && vacancy.salaryFrom <= filterTo))
        }
        _uiState.value = UiState.Success(filtered, allFavoriteIds)
    }

    class Factory(
        private val getVacanciesUseCase: GetVacanciesUseCase,
        private val getFavoritesUseCase: GetFavoritesUseCase? = null,
        private val addFavoriteUseCase: AddFavoriteUseCase? = null,
        private val removeFavoriteUseCase: RemoveFavoriteUseCase? = null,
        private val seekerId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            VacancyListViewModel(
                getVacanciesUseCase, getFavoritesUseCase,
                addFavoriteUseCase, removeFavoriteUseCase, seekerId
            ) as T
    }
}
