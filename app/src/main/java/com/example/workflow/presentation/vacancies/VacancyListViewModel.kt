package com.example.workflow.presentation.vacancies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.AddFavoriteUseCase
import com.example.workflow.domain.usecase.GetFavoritesUseCase
import com.example.workflow.domain.usecase.GetVacanciesUseCase
import com.example.workflow.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VacancyListViewModel(
    private val getVacanciesUseCase: GetVacanciesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase?,
    private val addFavoriteUseCase: AddFavoriteUseCase?,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase?,
    private val seekerId: String?
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val vacancies: List<VacancyResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedCity = MutableStateFlow("")
    val selectedEmploymentType = MutableStateFlow("")
    val salaryFrom = MutableStateFlow("")
    val salaryTo = MutableStateFlow("")

    private var allVacancies: List<VacancyResponseDto> = emptyList()
    private var allFavoriteIds: Set<String> = emptySet()

    init { loadVacancies() }

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

    // Вызывается при старте и когда что-то изменилось в избранных (например, удалено из FavoritesScreen)
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
        // Оптимистичное обновление — карточка уходит из списка сразу
        allFavoriteIds = allFavoriteIds + vacancyId
        applyFilters()

        viewModelScope.launch {
            runCatching { addFavoriteUseCase?.invoke(vacancyId) }
                .onFailure {
                    // Откат: возвращаем карточку в список
                    allFavoriteIds = allFavoriteIds - vacancyId
                    applyFilters()
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
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
            vacancy.id !in allFavoriteIds &&
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
        _uiState.value = UiState.Success(filtered)
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
