package com.mary.rutina.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mary.rutina.AppViewModel
import com.mary.rutina.data.PlanItem
import com.mary.rutina.data.RoutineData
import com.mary.rutina.ui.theme.*
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    viewModel: AppViewModel,
    dayId: String,
    onBack: () -> Unit
) {
    val day = RoutineData.weeklyPlan.find { it.id == dayId } ?: return
    val altToggles by viewModel.altToggles.collectAsState()
    val usaAlt = altToggles[dayId] == true
    val tracking by viewModel.todayTracking.collectAsState()
    val fatigaAlta by viewModel.fatigaAlta.collectAsState()
    var mostrarTracking by remember { mutableStateOf(false) }
    val esHoy = dayId == viewModel.todayDayId()

    // Cuando es día de deporte y NO se activó el switch de "no lo voy a hacer",
    // el plan mostrado son solo los extras opcionales (no la rutina completa).
    val mostrandoSoloActividad = day.esDeporte && !usaAlt
    val plan = viewModel.activePlanFor(dayId)
    val titulo = viewModel.activeTitleFor(dayId)
    val completados = viewModel.completedIndices()
    val progreso = if (plan.isNotEmpty()) completados.size.coerceAtMost(plan.size) else 0
    val actividadHecha = tracking?.actividadHecha == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(day.dia) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!esHoy) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SuperficieClara)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Vista previa de ${day.dia.lowercase()}. Solo se puede marcar como realizado el día que corresponde en el calendario.",
                            color = TextoSecundario, style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
                            "Tu FC promedio viene alta en tus últimas sesiones. Considera bajar intensidad o activar la rutina ALT hoy.",
                            color = TextoPrincipal, style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (mostrandoSoloActividad) {
                // ---- Tarjeta grande de la actividad del día (patinaje/fútbol/natación) ----
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(AzulAccion.copy(alpha = 0.15f))
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DirectionsRun, contentDescription = null, tint = AzulAccion)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                day.deporte?.replaceFirstChar { it.uppercase() } ?: day.titulo,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(day.subtitulo, color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Duración: ${day.duracion}",
                            color = AzulAccion,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.toggleActividadHecha(dayId, plan.size) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = esHoy,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (actividadHecha) VerdeOk else SuperficieClara
                            )
                        ) {
                            Icon(
                                if (actividadHecha) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (actividadHecha) "Actividad realizada" else "Marcar actividad como realizada")
                        }
                    }
                }

                if (plan.isNotEmpty()) {
                    item {
                        Text(
                            "Extras opcionales — $progreso/${plan.size} completados",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                item {
                    Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    Text(day.duracion + " • " + day.intensidad, color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Superficie)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Progreso de hoy", color = TextoPrincipal, fontWeight = FontWeight.Bold)
                        Text(
                            "$progreso/${plan.size} completados",
                            color = if (progreso >= plan.size && plan.isNotEmpty()) VerdeOk else AzulAccion,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (day.esDeporte) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Superficie)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("No voy a hacer ${day.deporte} hoy", color = TextoPrincipal, fontWeight = FontWeight.Bold)
                            Text("Actívalo para ver la rutina de casa completa", color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = usaAlt, onCheckedChange = { viewModel.toggleAlt(dayId) })
                    }
                }
            }

            itemsIndexed(plan) { index, item ->
                ExerciseRow(
                    item = item,
                    hecho = completados.contains(index),
                    habilitado = esHoy,
                    onToggleHecho = { viewModel.toggleExerciseDone(dayId, index, plan.size, mostrandoSoloActividad) }
                )
            }

            item {
                Button(
                    onClick = { mostrarTracking = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = esHoy,
                    colors = ButtonDefaults.buttonColors(containerColor = AzulAccion)
                ) {
                    Icon(Icons.Filled.MonitorHeart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (esHoy) "Registrar métricas del reloj" else "Disponible solo hoy")
                }
            }

            tracking?.let { t ->
                if (esHoy && (t.fcAvg != null || t.pasos != null || t.calorias != null || t.fcReposo != null || t.sinActividad)) {
                    item {
                        MetricsSummary(
                            t.fcReposo, t.fcAvg, t.fcMax, t.calorias, t.tiempoMin,
                            t.distanciaKm, t.suenoHoras, t.pasos, t.fuenteAuto, t.sinActividad
                        )
                    }
                }
            }
        }
    }

    if (mostrarTracking) {
        TrackingSheet(
            viewModel = viewModel,
            dayId = dayId,
            onDismiss = { mostrarTracking = false }
        )
    }
}

@Composable
private fun ExerciseRow(item: PlanItem, hecho: Boolean, habilitado: Boolean, onToggleHecho: () -> Unit) {
    val ejercicio = RoutineData.exerciseById(item.idLib)
    var expandido by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (hecho) VerdeOk.copy(alpha = 0.10f) else Superficie)
            .clickable { expandido = !expandido }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (ejercicio != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CategoryVisuals.colorFor(ejercicio.categoria).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (CategoryVisuals.localDrawableFor(ejercicio.id) != null) {
                        Icon(
                            painter = painterResource(CategoryVisuals.localDrawableFor(ejercicio.id)!!),
                            contentDescription = ejercicio.nombre,
                            tint = CategoryVisuals.colorFor(ejercicio.categoria),
                            modifier = Modifier.padding(6.dp)
                        )
                    } else if (ejercicio.imagenUrl != null) {
                        AsyncImage(
                            model = ejercicio.imagenUrl,
                            contentDescription = ejercicio.nombre,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            CategoryVisuals.iconFor(ejercicio.categoria),
                            contentDescription = null,
                            tint = CategoryVisuals.colorFor(ejercicio.categoria)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio?.nombre ?: item.idLib, color = TextoPrincipal, fontWeight = FontWeight.Bold)
                Text(item.tecnica, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(item.series, color = AzulAccion, fontWeight = FontWeight.Bold)
                Text("Descanso ${item.descanso}", color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("Rodilla: ${item.rodilla}", color = VerdeOk, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

        if (expandido && ejercicio != null) {
            Spacer(Modifier.height(10.dp))
            Text(ejercicio.musculos, color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(6.dp))
            ejercicio.bullets.forEach { b ->
                Text("• $b", color = TextoPrincipal, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(6.dp))
            Text(ejercicio.tecnica, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = {
                val query = URLEncoder.encode(ejercicio.nombre + " ejercicio técnica", "UTF-8")
                val url = "https://www.youtube.com/results?search_query=$query"
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = AzulAccion)
                Spacer(Modifier.width(6.dp))
                Text("Ver video", color = AzulAccion)
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onToggleHecho,
            modifier = Modifier.fillMaxWidth(),
            enabled = habilitado,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hecho) VerdeOk else SuperficieClara
            )
        ) {
            Icon(
                if (hecho) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(if (hecho) "Realizado" else if (habilitado) "Marcar como realizado" else "Disponible solo hoy")
        }
    }
}

@Composable
private fun MetricsSummary(
    fcReposo: Int?, fcAvg: Int?, fcMax: Int?, calorias: Int?, tiempoMin: Int?,
    distanciaKm: Double?, suenoHoras: Double?, pasos: Int?, fuenteAuto: Boolean, sinActividad: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Superficie)
            .padding(14.dp)
    ) {
        Text(
            if (fuenteAuto) "Desde el reloj (Health Connect)" else "Registro manual",
            color = TextoSecundario, style = MaterialTheme.typography.labelSmall
        )
        if (sinActividad) {
            Spacer(Modifier.height(4.dp))
            Text("Día sin actividad física (registrado)", color = NaranjaAlerta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        val filas = listOfNotNull(
            fcReposo?.let { "FC en reposo: $it" },
            fcAvg?.let { "FC promedio: $it" },
            fcMax?.let { "FC máx: $it" },
            calorias?.let { "Calorías: $it" },
            tiempoMin?.let { "Tiempo: $it min" },
            distanciaKm?.let { "Distancia: $it km" },
            suenoHoras?.let { "Sueño: $it h" },
            pasos?.let { "Pasos: $it" }
        )
        filas.forEach { Text(it, color = TextoPrincipal, style = MaterialTheme.typography.bodyMedium) }
    }
}
