package com.mary.rutina.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mary.rutina.AppViewModel
import com.mary.rutina.data.DayPlan
import com.mary.rutina.data.RoutineData
import com.mary.rutina.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekScreen(
    viewModel: AppViewModel,
    onDayClick: (String) -> Unit,
    onLibraryClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val fatigaAlta by viewModel.fatigaAlta.collectAsState()
    var mostrarTracking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutina de Mary", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Continuidad")
                    }
                    IconButton(onClick = onLibraryClick) {
                        Icon(Icons.Filled.LibraryBooks, contentDescription = "Biblioteca de ejercicios")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Ajustes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoOscuro)
            )
        },
        containerColor = FondoOscuro
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "Semana de rotación: ${viewModel.rotationWeek}/4",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (fatigaAlta) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NaranjaAlerta.copy(alpha = 0.15f))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = NaranjaAlerta)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "FC alta en tus últimas sesiones — considera un día más suave.",
                            color = TextoPrincipal, style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { mostrarTracking = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.MonitorHeart, contentDescription = null, tint = AzulAccion)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar datos de hoy (sin entrar a una rutina)", color = AzulAccion)
                }
            }
            items(RoutineData.weeklyPlan) { day ->
                DayCard(day = day, onClick = { onDayClick(day.id) })
            }
        }
    }

    if (mostrarTracking) {
        TrackingSheet(
            viewModel = viewModel,
            dayId = viewModel.todayDayId(),
            onDismiss = { mostrarTracking = false }
        )
    }
}

@Composable
private fun DayCard(day: DayPlan, onClick: () -> Unit) {
    val colorIntensidad = when (day.intensidad) {
        "ALTA" -> RojoAlta
        "MEDIA" -> NaranjaAlerta
        "FUERZA" -> AzulAccion
        else -> VerdeOk
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Superficie)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp, 42.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorIntensidad)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(day.dia, color = TextoSecundario, style = MaterialTheme.typography.labelMedium)
            Text(day.titulo, color = TextoPrincipal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(day.subtitulo, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(day.duracion, color = TextoPrincipal, style = MaterialTheme.typography.labelLarge)
            Text(day.intensidad, color = colorIntensidad, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
