package com.mary.rutina.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mary.rutina.AppViewModel
import com.mary.rutina.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var mostrarPrimeraConfirmacion by remember { mutableStateOf(false) }
    var mostrarSegundaConfirmacion by remember { mutableStateOf(false) }
    var borrado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Superficie)
                    .padding(16.dp)
            ) {
                Text("Historial de entrenamientos", color = TextoPrincipal, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Borra todos los registros guardados: calendario de continuidad, métricas del reloj y días marcados como realizados. La rutina y la biblioteca de ejercicios no se ven afectadas.",
                    color = TextoSecundario, style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { mostrarPrimeraConfirmacion = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoAlta)
                ) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Borrar historial")
                }
                if (borrado) {
                    Spacer(Modifier.height(10.dp))
                    Text("Historial borrado.", color = VerdeOk, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (mostrarPrimeraConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarPrimeraConfirmacion = false },
            title = { Text("¿Borrar todo el historial?") },
            text = { Text("Se perderá el calendario de continuidad y todas las métricas guardadas hasta hoy. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarPrimeraConfirmacion = false
                    mostrarSegundaConfirmacion = true
                }) { Text("Continuar", color = RojoAlta) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarPrimeraConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarSegundaConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarSegundaConfirmacion = false },
            title = { Text("Última confirmación") },
            text = { Text("Estás a punto de borrar TODO el historial de forma permanente. ¿Confirmas?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.borrarHistorialCompleto()
                    mostrarSegundaConfirmacion = false
                    borrado = true
                }) { Text("Sí, borrar todo", color = RojoAlta, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSegundaConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }
}
