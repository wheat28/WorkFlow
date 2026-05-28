package com.example.workflow.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workflow.data.remote.dto.ApplicationResponseDto
import com.example.workflow.domain.usecase.application.GetVacancyApplicationsUseCase
import com.example.workflow.domain.usecase.application.UpdateApplicationStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class VacancyApplicationsViewModel(
    private val getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val vacancyId: String
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val applications: List<ApplicationResponseDto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _allApplications = MutableStateFlow<List<ApplicationResponseDto>>(emptyList())
    private val _loadState = MutableStateFlow<UiState>(UiState.Loading)
    val filterStatus = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_allApplications, filterStatus, _loadState) { apps, filter, loadState ->
                when (loadState) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> loadState
                    is UiState.Success -> UiState.Success(
                        if (filter == null) apps else apps.filter { it.status == filter }
                    )
                }
            }.collect { _uiState.value = it }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            runCatching { getVacancyApplicationsUseCase(vacancyId) }
                .onSuccess {
                    _allApplications.value = it
                    _loadState.value = UiState.Success(it)
                }
                .onFailure { _loadState.value = UiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun setFilter(status: String?) {
        filterStatus.value = status
    }

    fun updateStatus(applicationId: String, status: String) {
        val updated = _allApplications.value.map {
            if (it.id == applicationId) it.copy(status = status) else it
        }
        val previous = _allApplications.value
        _allApplications.value = updated
        viewModelScope.launch {
            runCatching { updateApplicationStatusUseCase(applicationId, status) }
                .onFailure { _allApplications.value = previous }
        }
    }

    class Factory(
        private val getVacancyApplicationsUseCase: GetVacancyApplicationsUseCase,
        private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
        private val vacancyId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            VacancyApplicationsViewModel(
                getVacancyApplicationsUseCase, updateApplicationStatusUseCase, vacancyId
            ) as T
    }
}
