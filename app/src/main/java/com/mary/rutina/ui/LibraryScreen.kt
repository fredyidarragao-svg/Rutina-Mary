package com.mary.rutina.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
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
import com.mary.rutina.data.Exercise
import com.mary.rutina.data.RoutineData
import com.mary.rutina.ui.theme.*
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val filtro by viewModel.libraryFilter.collectAsState()
    val ejercicios = if (filtro == "TODOS") RoutineData.exerciseLibrary
        else RoutineData.exerciseLibrary.filter { it.categoria == filtro }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca de ejercicios") },
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
        Column(modifier = Modifier.padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(RoutineData.categorias) { cat ->
                    FilterChip(
                        selected = filtro == cat,
                        onClick = { viewModel.setLibraryFilter(cat) },
                        label = { Text(cat) }
                    )
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ejercicios) { ex -> ExerciseCard(ex) }
            }
        }
    }
}

@Composable
private fun ExerciseCard(ex: Exercise) {
    var expandido by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Superficie)
            .clickableExpand { expandido = !expandido }
            .padding(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CategoryVisuals.colorFor(ex.categoria).copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                if (CategoryVisuals.localDrawableFor(ex.id) != null) {
                    Icon(
                        painter = painterResource(CategoryVisuals.localDrawableFor(ex.id)!!),
                        contentDescription = ex.nombre,
                        tint = CategoryVisuals.colorFor(ex.categoria),
                        modifier = Modifier.padding(6.dp)
                    )
                } else if (ex.imagenUrl != null) {
                    AsyncImage(
                        model = ex.imagenUrl,
                        contentDescription = ex.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(CategoryVisuals.iconFor(ex.categoria), contentDescription = null, tint = CategoryVisuals.colorFor(ex.categoria))
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ex.nombre, color = TextoPrincipal, fontWeight = FontWeight.Bold)
                Text(ex.subtitulo, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            Text(ex.seriesRecom, color = AzulAccion, style = MaterialTheme.typography.labelMedium)
        }
        if (ex.usaKettlebell) {
            Spacer(Modifier.height(4.dp))
            Text("Usa mango kettlebell", color = NaranjaAlerta, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        if (expandido) {
            Spacer(Modifier.height(8.dp))
            Text(ex.musculos, color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(6.dp))
            ex.bullets.forEach { Text("• $it", color = TextoPrincipal, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(6.dp))
            Text(ex.tecnica, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
            Text("Rodilla: ${ex.rodilla}", color = VerdeOk, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = {
                val query = URLEncoder.encode(ex.nombre + " ejercicio técnica", "UTF-8")
                val url = "https://www.youtube.com/results?search_query=$query"
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = AzulAccion)
                Spacer(Modifier.width(6.dp))
                Text("Ver video", color = AzulAccion)
            }
        }
    }
}

private fun Modifier.clickableExpand(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
