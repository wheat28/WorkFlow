package com.example.workflow.presentation.vacancies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.workflow.presentation.common.VacancyListSkeleton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.favorite.AddFavoriteUseCase
import com.example.workflow.domain.usecase.favorite.GetFavoritesUseCase
import com.example.workflow.domain.usecase.vacancy.GetVacanciesUseCase
import com.example.workflow.domain.usecase.favorite.RemoveFavoriteUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90
import kotlinx.coroutines.launch

private val employmentTypes = listOf("", "FULL_TIME", "PART_TIME", "REMOTE", "INTERNSHIP")

private fun employmentLabel(type: String) = when (type) {
    "FULL_TIME" -> "Полная"
    "PART_TIME" -> "Частичная"
    "REMOTE" -> "Удалённо"
    "INTERNSHIP" -> "Стажировка"
    else -> "Все типы"
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
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedEmploymentType by viewModel.selectedEmploymentType.collectAsStateWithLifecycle()
    val salaryFrom by viewModel.salaryFrom.collectAsStateWithLifecycle()
    val salaryTo by viewModel.salaryTo.collectAsStateWithLifecycle()

    val hasActiveFilters = selectedCity.isNotBlank() || selectedEmploymentType.isNotBlank()
            || salaryFrom.isNotBlank() || salaryTo.isNotBlank()

    var showFilters by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(favoritesRemovedKey) {
        if (favoritesRemovedKey > 0) viewModel.loadFavorites()
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState
        ) {
            FilterSheetContent(
                selectedCity = selectedCity,
                onCityChange = { viewModel.onCitySelected(it) },
                selectedEmploymentType = selectedEmploymentType,
                onEmploymentTypeChange = { viewModel.onEmploymentTypeSelected(it) },
                salaryFrom = salaryFrom,
                onSalaryFromChange = { viewModel.onSalaryFromChanged(it.filter(Char::isDigit)) },
                salaryTo = salaryTo,
                onSalaryToChange = { viewModel.onSalaryToChanged(it.filter(Char::isDigit)) },
                hasActiveFilters = hasActiveFilters,
                onClear = { viewModel.clearFilters() },
                onApply = { scope.launch { sheetState.hide() }.invokeOnCompletion { showFilters = false } }
            )
        }
    }

    val searchGradient = Brush.linearGradient(
        colors = listOf(com.example.workflow.ui.theme.Indigo50, androidx.compose.ui.graphics.Color(0xFF9B8FF5)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp)
                .border(width = 1.5.dp, brush = searchGradient, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Должность, компания...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            BadgedBox(
                badge = { if (hasActiveFilters) Badge(containerColor = Coral40) },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(onClick = { showFilters = true }) {
                    Icon(
                        Icons.Outlined.Tune,
                        contentDescription = "Фильтры",
                        tint = if (hasActiveFilters) Indigo60 else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = uiState) {
                is VacancyListViewModel.UiState.Loading -> VacancyListSkeleton()
                is VacancyListViewModel.UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is VacancyListViewModel.UiState.Success -> {
                    if (state.vacancies.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Work,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Вакансии не найдены",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.vacancies, key = { it.id }) { vacancy ->
                                VacancyCard(
                                    vacancy = vacancy,
                                    isFavorite = vacancy.id in state.favoriteIds,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheetContent(
    selectedCity: String,
    onCityChange: (String) -> Unit,
    selectedEmploymentType: String,
    onEmploymentTypeChange: (String) -> Unit,
    salaryFrom: String,
    onSalaryFromChange: (String) -> Unit,
    salaryTo: String,
    onSalaryToChange: (String) -> Unit,
    hasActiveFilters: Boolean,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo60,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline
    )
    val fieldShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Фильтры", style = MaterialTheme.typography.titleMedium)
            if (hasActiveFilters) {
                TextButton(onClick = onClear) {
                    Text("Сбросить", color = Coral40)
                }
            }
        }

        OutlinedTextField(
            value = selectedCity,
            onValueChange = onCityChange,
            label = { Text("Город") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = fieldShape,
            colors = fieldColors
        )

        EmploymentTypeDropdown(
            selected = selectedEmploymentType,
            onSelected = onEmploymentTypeChange,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = salaryFrom,
                onValueChange = onSalaryFromChange,
                label = { Text("Зарплата от") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = fieldShape,
                colors = fieldColors
            )
            OutlinedTextField(
                value = salaryTo,
                onValueChange = onSalaryToChange,
                label = { Text("до") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = fieldShape,
                colors = fieldColors
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo60)
        ) {
            Text("Применить", style = MaterialTheme.typography.labelLarge)
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

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = employmentLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text("Тип занятости") },
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

@Composable
private fun VacancyCard(
    vacancy: VacancyResponseDto,
    isFavorite: Boolean,
    onToggleFavorite: (() -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Убрать из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) Coral40 else MaterialTheme.colorScheme.onSurfaceVariant,
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
