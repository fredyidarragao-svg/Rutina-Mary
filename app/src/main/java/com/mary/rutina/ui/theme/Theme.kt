package com.mary.rutina.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaOscuro = darkColorScheme(
    primary = AzulAccion,
    background = FondoOscuro,
    surface = Superficie,
    surfaceVariant = SuperficieClara,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    onPrimary = Color.White,
    error = RojoAlta
)

@Composable
fun RutinaMaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        typography = MaterialTheme.typography,
        content = content
    )
}
