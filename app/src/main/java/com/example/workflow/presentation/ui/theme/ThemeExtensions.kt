package com.example.workflow.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MaterialTheme.navigationBarContainerColor: Color
    @Composable get() = if (isSystemInDarkTheme()) colorScheme.surface else Indigo95
