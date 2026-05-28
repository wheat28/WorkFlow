package com.example.workflow.presentation.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(): Brush {
    val colors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    return Brush.linearGradient(colors, start = Offset(offset - 600f, 0f), end = Offset(offset, 0f))
}

@Composable
fun VacancyCardSkeleton() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(Modifier.fillMaxWidth(0.6f).height(18.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.width(64.dp).height(18.dp).background(brush, RoundedCornerShape(8.dp)))
            }
            Box(Modifier.fillMaxWidth(0.4f).height(13.dp).background(brush, RoundedCornerShape(4.dp)))
            Box(Modifier.fillMaxWidth(0.5f).height(13.dp).background(brush, RoundedCornerShape(4.dp)))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(80.dp).height(24.dp).background(brush, RoundedCornerShape(12.dp)))
                Box(Modifier.width(70.dp).height(24.dp).background(brush, RoundedCornerShape(12.dp)))
            }
        }
    }
}

@Composable
fun ApplicationCardSkeleton() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(Modifier.fillMaxWidth(0.55f).height(18.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.width(80.dp).height(18.dp).background(brush, RoundedCornerShape(8.dp)))
            }
            Box(Modifier.width(110.dp).height(12.dp).background(brush, RoundedCornerShape(4.dp)))
            Box(Modifier.fillMaxWidth().height(12.dp).background(brush, RoundedCornerShape(4.dp)))
            Box(Modifier.fillMaxWidth(0.7f).height(12.dp).background(brush, RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
fun EmployerVacancyCardSkeleton() {
    val brush = shimmerBrush()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(Modifier.fillMaxWidth(0.55f).height(18.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.width(64.dp).height(18.dp).background(brush, RoundedCornerShape(8.dp)))
            }
            Box(Modifier.fillMaxWidth(0.35f).height(13.dp).background(brush, RoundedCornerShape(4.dp)))
            Box(Modifier.width(80.dp).height(24.dp).background(brush, RoundedCornerShape(12.dp)))
        }
    }
}

@Composable
fun VacancyDetailSkeleton(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.fillMaxWidth(0.75f).height(24.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.fillMaxWidth(0.45f).height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.fillMaxWidth(0.3f).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth(0.4f).height(20.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.width(90.dp).height(28.dp).background(brush, RoundedCornerShape(14.dp)))
                    Box(Modifier.width(80.dp).height(28.dp).background(brush, RoundedCornerShape(14.dp)))
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.width(80.dp).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.fillMaxWidth().height(12.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.fillMaxWidth().height(12.dp).background(brush, RoundedCornerShape(4.dp)))
                Box(Modifier.fillMaxWidth(0.6f).height(12.dp).background(brush, RoundedCornerShape(4.dp)))
            }
        }
    }
}

@Composable
fun ResumeDetailSkeleton(modifier: Modifier = Modifier) = VacancyDetailSkeleton(modifier)

@Composable
fun VacancyListSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(5) { VacancyCardSkeleton() }
    }
}

@Composable
fun ApplicationsListSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(4) { ApplicationCardSkeleton() }
    }
}

@Composable
fun EmployerVacanciesListSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(4) { EmployerVacancyCardSkeleton() }
    }
}
