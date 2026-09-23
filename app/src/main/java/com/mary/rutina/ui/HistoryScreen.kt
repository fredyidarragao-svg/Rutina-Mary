package com.mary.rutina.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mary.rutina.AppViewModel
import com.mary.rutina.data.RoutineData
import com.mary.rutina.data.TrackingEntity
import com.mary.rutina.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class EstadoDia { COMPLETO, PARCIAL, SIN_ACTIVIDAD, NO_HECHO, FUTURO }

private fun estadoDe(entidad: TrackingEntity?, fecha: LocalDate): EstadoDia {
    if (fecha.isAfter(LocalDate.now())) return EstadoDia.FUTURO
    if (entidad == null) return EstadoDia.NO_HECHO
    if (entidad.sinActividad) return EstadoDia.SIN_ACTIVIDAD
    if (entidad.hechoHoy) return EstadoDia.COMPLETO
    val algoDeProgreso = entidad.actividadHecha || entidad.completados.isNotBlank()
    return if (algoDeProgreso) EstadoDia.PARCIAL else EstadoDia.NO_HECHO
}

private fun colorDe(estado: EstadoDia): androidx.compose.ui.graphics.Color = when (estado) {
    EstadoDia.COMPLETO -> VerdeOk
    EstadoDia.PARCIAL -> NaranjaAlerta
    EstadoDia.SIN_ACTIVIDAD -> TextoSecundario
    EstadoDia.NO_HECHO -> RojoAlta
    EstadoDia.FUTURO -> SuperficieClara
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var mes by remember { mutableStateOf(YearMonth.now()) }
    val historial by viewModel.historialMes.collectAsState()
    var diaSeleccionado by remember { mutableStateOf<LocalDate?>(null) }
    val fechaFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    LaunchedEffect(mes) { viewModel.cargarHistorialMes(mes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Continuidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoOscuro)
            )
        },
        containerColor = FondoOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Selector de mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { mes = mes.minusMonths(1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mes anterior", tint = TextoPrincipal)
                }
                Text(
                    mes.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() } + " ${mes.year}",
                    color = TextoPrincipal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { mes = mes.plusMonths(1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Mes siguiente", tint = TextoPrincipal)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Leyenda
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Leyenda(VerdeOk, "Completo")
                Leyenda(NaranjaAlerta, "Parcial")
                Leyenda(RojoAlta, "No hecho")
                Leyenda(TextoSecundario, "Sin actividad")
            }

            Spacer(Modifier.height(16.dp))

            // Encabezado de días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                    Text(it, color = TextoSecundario, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            Spacer(Modifier.height(6.dp))

            val primerDia = mes.atDay(1)
            val offsetInicial = (primerDia.dayOfWeek.value - 1) // lunes=0
            val totalDias = mes.lengthOfMonth()
            val celdas = (1..totalDias).map { mes.atDay(it) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(offsetInicial) { Box(modifier = Modifier.aspectRatio(1f)) }
                items(celdas) { fecha ->
                    val entidad = historial[fecha.format(fechaFormatter)]
                    val estado = estadoDe(entidad, fecha)
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorDe(estado).copy(alpha = if (estado == EstadoDia.FUTURO) 0.3f else 0.85f))
                            .clickable(enabled = entidad != null) { diaSeleccionado = fecha },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            fecha.dayOfMonth.toString(),
                            color = if (estado == EstadoDia.FUTURO) TextoSecundario else androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Toca un día con color para ver el detalle registrado.",
                color = TextoSecundario, style = MaterialTheme.typography.bodySmall
            )
        }
    }

    diaSeleccionado?.let { fecha ->
        val entidad = historial[fecha.format(fechaFormatter)]
        if (entidad != null) {
            DiaDetalleDialog(fecha = fecha, entidad = entidad, onDismiss = { diaSeleccionado = null })
        }
    }
}

@Composable
private fun Leyenda(color: androidx.compose.ui.graphics.Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(texto, color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DiaDetalleDialog(fecha: LocalDate, entidad: TrackingEntity, onDismiss: () -> Unit) {
    val dayPlan = RoutineData.weeklyPlan.find { it.id == entidad.diaId }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
        title = { Text(fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es"))).replaceFirstChar { it.uppercase() }) },
        text = {
            Column {
                dayPlan?.let { Text(it.titulo, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(8.dp))
                if (entidad.sinActividad) {
                    Text("Registrado como día sin actividad física.")
                    Spacer(Modifier.height(6.dp))
                }
                if (entidad.actividadHecha) Text("✓ Actividad principal realizada")
                if (entidad.completados.isNotBlank()) {
                    Text("Ejercicios extra marcados: ${entidad.completados.split(",").filter { it.isNotBlank() }.size}")
                }
                Spacer(Modifier.height(8.dp))
                listOfNotNull(
                    entidad.fcReposo?.let { "FC en reposo: $it" },
                    entidad.fcAvg?.let { "FC promedio: $it" },
                    entidad.fcMax?.let { "FC máx: $it" },
                    entidad.calorias?.let { "Calorías: $it" },
                    entidad.tiempoMin?.let { "Tiempo: $it min" },
                    entidad.distanciaKm?.let { "Distancia: $it km" },
                    entidad.suenoHoras?.let { "Sueño: $it h" },
                    entidad.pasos?.let { "Pasos: $it" }
                ).forEach { Text(it) }
            }
        }
    )
}
