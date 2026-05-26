package com.example.workflow.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.remote.dto.VacancyResponseDto
import com.example.workflow.domain.usecase.GetFavoritesUseCase
import com.example.workflow.domain.usecase.RemoveFavoriteUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Green40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@Composable
fun FavoritesScreen(
    getFavoritesUseCase: GetFavoritesUseCase,
    removeFavoriteUseCase: RemoveFavoriteUseCase,
    seekerId: String,
    onVacancyClick: (String) -> Unit,
    onFavoriteRemoved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModel.Factory(getFavoritesUseCase, removeFavoriteUseCase, seekerId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Перезагружаем список каждый раз когда вкладка становится видимой
    LaunchedEffect(Unit) {
        viewModel.reload()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is FavoritesViewModel.UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Indigo60)
            }
            is FavoritesViewModel.UiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            is FavoritesViewModel.UiState.Success -> {
                if (state.vacancies.isEmpty()) {
                    Text(
                        text = "Нет сохранённых вакансий",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.vacancies, key = { it.id }) { vacancy ->
                            FavoriteVacancyCard(
                                vacancy = vacancy,
                                onClick = { onVacancyClick(vacancy.id) },
                                onRemove = {
                                    viewModel.remove(vacancy.id)
                                    onFavoriteRemoved()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteVacancyCard(
    vacancy: VacancyResponseDto,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = vacancy.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = vacancy.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Indigo60
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Удалить из избранного",
                        tint = Coral40,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (vacancy.salaryFrom != null || vacancy.salaryTo != null) {
                val salary = buildString {
                    if (vacancy.salaryFrom != null) append("от ${vacancy.salaryFrom}")
                    if (vacancy.salaryTo != null) append(" до ${vacancy.salaryTo}")
                    if (vacancy.currency != null) append(" ${vacancy.currency}")
                }
                Text(text = salary, style = MaterialTheme.typography.bodySmall, color = Green40)
            }

            vacancy.city?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.size(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(vacancy.employmentType, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Indigo90, labelColor = Indigo60),
                    border = null
                )
            }
        }
    }
}
