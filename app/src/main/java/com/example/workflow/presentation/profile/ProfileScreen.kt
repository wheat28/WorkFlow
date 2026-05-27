package com.example.workflow.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workflow.data.local.TokenDataStore
import com.example.workflow.data.remote.dto.EmployerResponseDto
import com.example.workflow.data.remote.dto.ResumeResponseDto
import com.example.workflow.domain.usecase.employer.GetEmployerByIdUseCase
import com.example.workflow.domain.usecase.resume.GetMyResumesUseCase
import com.example.workflow.ui.theme.Coral40
import com.example.workflow.ui.theme.Indigo60
import com.example.workflow.ui.theme.Indigo90

@Composable
fun ProfileScreen(
    tokenDataStore: TokenDataStore,
    getMyResumesUseCase: GetMyResumesUseCase,
    getEmployerByIdUseCase: GetEmployerByIdUseCase? = null,
    onLogout: () -> Unit,
    onCreateResume: () -> Unit,
    onEditResume: (String) -> Unit,
    onEditEmployerProfile: (() -> Unit)? = null,
    onEditSeekerProfile: (() -> Unit)? = null,
    resumeRefreshKey: Int = 0,
    employerProfileRefreshKey: Int = 0,
    seekerProfileRefreshKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val userType by tokenDataStore.userTypeFlow.collectAsState(initial = null)
    val userId by tokenDataStore.userIdFlow.collectAsState(initial = null)
    val displayName by tokenDataStore.displayNameFlow.collectAsState(initial = null)

    val roleLabel = when (userType) {
        "SEEKER" -> "Соискатель"
        "EMPLOYER" -> "Работодатель"
        else -> ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                .border(1.5.dp, Indigo60.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Indigo90, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName?.firstOrNull()?.uppercaseChar()?.toString() ?: if (userType == "SEEKER") "С" else "Р",
                        style = MaterialTheme.typography.titleLarge,
                        color = Indigo60
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = displayName ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = roleLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (userType == "SEEKER" && userId != null) {
            SeekerProfileContent(
                userId = userId!!,
                getMyResumesUseCase = getMyResumesUseCase,
                resumeRefreshKey = resumeRefreshKey,
                onCreateResume = onCreateResume,
                onEditResume = onEditResume,
                onEditProfile = onEditSeekerProfile,
                onLogout = onLogout,
                modifier = Modifier.weight(1f)
            )
        } else if (userType == "EMPLOYER" && userId != null && getEmployerByIdUseCase != null) {
            EmployerProfileContent(
                userId = userId!!,
                getEmployerByIdUseCase = getEmployerByIdUseCase,
                refreshKey = employerProfileRefreshKey,
                onEditProfile = onEditEmployerProfile,
                onLogout = onLogout,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
            LogoutButton(onLogout)
        }
    }
}

@Composable
private fun SeekerProfileContent(
    userId: String,
    getMyResumesUseCase: GetMyResumesUseCase,
    resumeRefreshKey: Int,
    onCreateResume: () -> Unit,
    onEditResume: (String) -> Unit,
    onEditProfile: (() -> Unit)?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(getMyResumesUseCase, userId)
    )
    val resumeState by profileViewModel.resumeState.collectAsStateWithLifecycle()

    LaunchedEffect(resumeRefreshKey) {
        if (resumeRefreshKey > 0) profileViewModel.loadResumes()
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Мои резюме",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onCreateResume,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Indigo60)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Создать", color = Indigo60, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            when (val state = resumeState) {
                is ProfileViewModel.ResumeState.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Indigo60)
                    }
                }
                is ProfileViewModel.ResumeState.Error -> item {
                    Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                is ProfileViewModel.ResumeState.Success -> {
                    if (state.resumes.isEmpty()) {
                        item {
                            Text(
                                "Резюме ещё нет. Создайте первое!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(state.resumes) { resume -> ResumeCard(resume, onClick = { onEditResume(resume.id) }) }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (onEditProfile != null) {
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo60),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo60)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Редактировать профиль", style = MaterialTheme.typography.labelLarge)
                }
            }
            LogoutButton(onLogout)
        }
    }
}

@Composable
private fun EmployerProfileContent(
    userId: String,
    getEmployerByIdUseCase: GetEmployerByIdUseCase,
    refreshKey: Int,
    onEditProfile: (() -> Unit)?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var employer by remember { mutableStateOf<EmployerResponseDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(refreshKey) {
        loading = true
        runCatching { getEmployerByIdUseCase(userId) }
            .onSuccess { employer = it }
            .onFailure { }
        loading = false
    }

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Indigo60)
                }
            } else {
                employer?.let { e ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "О компании",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!e.description.isNullOrBlank()) {
                                Text(
                                    text = e.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!e.city.isNullOrBlank() || !e.industry.isNullOrBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!e.city.isNullOrBlank()) InfoChip(e.city)
                                    if (!e.industry.isNullOrBlank()) InfoChip(e.industry)
                                }
                            }
                            if (!e.website.isNullOrBlank()) {
                                InfoRow(label = "Сайт", value = e.website)
                            }
                            if (!e.phone.isNullOrBlank()) {
                                InfoRow(label = "Телефон", value = e.phone)
                            }
                            if (!e.email.isNullOrBlank()) {
                                InfoRow(label = "Email", value = e.email)
                            }
                            if (e.description.isNullOrBlank() && e.city.isNullOrBlank() &&
                                e.industry.isNullOrBlank() && e.website.isNullOrBlank() && e.phone.isNullOrBlank()
                            ) {
                                Text(
                                    text = "Заполните информацию о компании",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (onEditProfile != null) {
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo60),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo60)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Редактировать профиль", style = MaterialTheme.typography.labelLarge)
                }
            }
            LogoutButton(onLogout)
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Indigo60.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Indigo60,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Coral40)
    ) {
        Text("Выйти из аккаунта", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ResumeCard(resume: ResumeResponseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    resume.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (resume.isActive) Indigo60.copy(alpha = 0.15f) else Coral40.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (resume.isActive) "Активно" else "Скрыто",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (resume.isActive) Indigo60 else Coral40,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(resume.position, style = MaterialTheme.typography.bodyMedium, color = Indigo60)
            if (resume.salaryExpected != null) {
                Text(
                    "от ${resume.salaryExpected} ${resume.currency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            resume.city?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
