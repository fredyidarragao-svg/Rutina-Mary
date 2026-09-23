package com.mary.rutina.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.ui.graphics.vector.ImageVector
import com.mary.rutina.R
import com.mary.rutina.ui.theme.AzulAccion
import com.mary.rutina.ui.theme.NaranjaAlerta
import com.mary.rutina.ui.theme.RojoAlta
import com.mary.rutina.ui.theme.VerdeOk
import androidx.compose.ui.graphics.Color

/**
 * Ícono + color de miniatura según la categoría del ejercicio, para reconocer de un
 * vistazo el tipo de movimiento sin tener que abrir el video. No reemplaza una
 * ilustración paso a paso del ejercicio exacto, solo ayuda a ubicar rápido de qué se trata.
 */
object CategoryVisuals {

    /** Pictograma dibujado a mano para ejercicios sin foto real disponible. */
    fun localDrawableFor(exerciseId: String): Int? = when (exerciseId) {
        "plancha-toques" -> R.drawable.ic_plank_taps
        "patada-gluteo-cuadrupedia" -> R.drawable.ic_donkey_kick
        else -> null
    }

    fun iconFor(categoria: String): ImageVector = when (categoria) {
        "GLUTEO" -> Icons.Filled.SportsGymnastics
        "PIERNA" -> Icons.Filled.DirectionsRun
        "ABDOMEN" -> Icons.Filled.SelfImprovement
        "BRAZOS" -> Icons.Filled.FitnessCenter
        else -> Icons.Filled.DirectionsBike
    }

    fun colorFor(categoria: String): Color = when (categoria) {
        "GLUTEO" -> RojoAlta
        "PIERNA" -> VerdeOk
        "ABDOMEN" -> NaranjaAlerta
        "BRAZOS" -> AzulAccion
        else -> AzulAccion
    }
}
