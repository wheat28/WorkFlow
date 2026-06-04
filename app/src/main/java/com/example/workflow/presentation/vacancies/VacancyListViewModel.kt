package com.example.workflow.presentation.vacancies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.domain.model.Vacancy
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.favorite.GetFavoritesUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacanciesUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class VacancyListViewModel @Inject constructor(
    private val getVacanciesUseCase: GetVacanciesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val vacancies: List<Vacancy>,
            val favoriteIds: Set<String>,
            val canToggleFavorite: Boolean
        ) : UiState()
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

    private var allVacancies: List<Vacancy> = emptyList()
    private var allFavoriteIds: Set<String> = emptySet()
    private var seekerId: String? = null

    init {
        viewModelScope.launch {
            val userType = tokenDataStore.getUserType()
            seekerId = if (userType == "SEEKER") tokenDataStore.getUserId() else null
            loadVacancies()
        }
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
                    val id = seekerId
                    if (id != null) {
                        runCatching { getFavoritesUseCase(id) }
                            .onSuccess { favs -> allFavoriteIds = favs.map { it.id }.toSet() }
                    }
                    applyFilters()
                }
            _isRefreshing.value = false
        }
    }

    fun loadFavorites() {
        val id = seekerId
        if (id == null) {
            applyFilters()
            return
        }
        viewModelScope.launch {
            runCatching { getFavoritesUseCase(id) }
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
                runCatching { removeFavoriteUseCase(vacancyId) }
            } else {
                runCatching { addFavoriteUseCase(vacancyId) }
            }
            result.onFailure {
                allFavoriteIds = if (wasFavorite) allFavoriteIds + vacancyId else allFavoriteIds - vacancyId
                applyFilters()
            }
        }
    }

    fun onSearchQueryChanged(query: String) { searchQuery.value = query }

    fun clearFilters() {
        selectedCity.value = ""
        selectedEmploymentType.value = ""
        salaryFrom.value = ""
        salaryTo.value = ""
        applyFilters()
    }

    fun onCitySelected(city: String) { selectedCity.value = city; applyFilters() }
    fun onEmploymentTypeSelected(type: String) { selectedEmploymentType.value = type; applyFilters() }
    fun onSalaryFromChanged(value: String) { salaryFrom.value = value; applyFilters() }
    fun onSalaryToChanged(value: String) { salaryTo.value = value; applyFilters() }

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
        _uiState.value = UiState.Success(filtered, allFavoriteIds, seekerId != null)
    }
}
