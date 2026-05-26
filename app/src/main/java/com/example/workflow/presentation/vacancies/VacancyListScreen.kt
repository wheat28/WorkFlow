package com.example.workflow.presentation.vacancies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.AddFavoriteUseCase
import com.example.workflow.domain.usecase.GetFavoritesUseCase
import com.example.workflow.domain.usecase.GetVacanciesUseCase
import com.example.workflow.domain.usecase.RemoveFavoriteUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

private val employmentTypes = listOf("", "FULL_TIME", "PART_TIME", "REMOTE", "INTERNSHIP")

private fun employmentLabel(type: String) = when (type) {
    "FULL_TIME" -> "Полная"
    "PART_TIME" -> "Частичная"
    "REMOTE" -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else -> "Все"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyListScreen(
    getVacanciesUseCase: GetVacanciesUseCase,
    onVacancyClick: (String) -> Unit,
    getFavoritesUseCase: GetFavoritesUseCase? = null,
    addFavoriteUseCase: AddFavoriteUseCase? = null,
    removeFavoriteUseCase: RemoveFavoriteUseCase? = null,
    seekerId: String? = null,
    favoritesRemovedKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val viewModel: VacancyListViewModel = viewModel(
        factory = VacancyListViewModel.Factory(
            getVacanciesUseCase, getFavoritesUseCase, addFavoriteUseCase, removeFavoriteUseCase, seekerId
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedEmploymentType by viewModel.selectedEmploymentType.collectAsStateWithLifecycle()
    val salaryFrom by viewModel.salaryFrom.collectAsStateWithLifecycle()
    val salaryTo by viewModel.salaryTo.collectAsStateWithLifecycle()

    // Обновляем избранное только когда FavoritesScreen сигнализирует об удалении
    LaunchedEffect(favoritesRemovedKey) {
        if (favoritesRemovedKey > 0) viewModel.loadFavorites()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Должность, компания...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo60,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = { viewModel.onCitySelected(it) },
                    placeholder = { Text("Город") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo60,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                EmploymentTypeDropdown(
                    selected = selectedEmploymentType,
                    onSelected = { viewModel.onEmploymentTypeSelected(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = salaryFrom,
                    onValueChange = { viewModel.onSalaryFromChanged(it.filter(Char::isDigit)) },
                    placeholder = { Text("Зарплата от") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo60,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                OutlinedTextField(
                    value = salaryTo,
                    onValueChange = { viewModel.onSalaryToChanged(it.filter(Char::isDigit)) },
                    placeholder = { Text("до") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo60,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        when (val state = uiState) {
            is VacancyListViewModel.UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            }
            is VacancyListViewModel.UiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is VacancyListViewModel.UiState.Success -> {
                if (state.vacancies.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Вакансии не найдены",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.vacancies, key = { it.id }) { vacancy ->
                            VacancyCard(
                                vacancy = vacancy,
                                onToggleFavorite = if (seekerId != null) {
                                    { viewModel.toggleFavorite(vacancy.id) }
                                } else null,
                                onClick = { onVacancyClick(vacancy.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VacancyCard(
    vacancy: VacancyResponseDto,
    onToggleFavorite: (() -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(start = 18.dp, end = 8.dp, top = 14.dp, bottom = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = vacancy.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = vacancy.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Indigo60
                    )
                    if (vacancy.salaryFrom != null || vacancy.salaryTo != null) {
                        val salary = buildString {
                            if (vacancy.salaryFrom != null) append("от ${vacancy.salaryFrom}")
                            if (vacancy.salaryTo != null) append(" до ${vacancy.salaryTo}")
                            if (vacancy.currency != null) append(" ${vacancy.currency}")
                        }
                        Text(
                            text = salary,
                            style = MaterialTheme.typography.titleSmall,
                            color = Green40
                        )
                    }
                }

                if (onToggleFavorite != null) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Добавить в избранное",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!vacancy.city.isNullOrBlank()) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(vacancy.city, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = null
                    )
                }
                SuggestionChip(
                    onClick = {},
                    label = { Text(employmentLabel(vacancy.employmentType), style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Indigo90,
                        labelColor = Indigo60
                    ),
                    border = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmploymentTypeDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it },
        modifier = modifier) {
        OutlinedTextField(
            value = employmentLabel(selected),
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Тип") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Indigo60,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            employmentTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(employmentLabel(type)) },
                    onClick = { onSelected(type); expanded = false }
                )
            }
        }
    }
}
