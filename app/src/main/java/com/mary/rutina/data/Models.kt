package com.mary.rutina.data

/** Un ejercicio de la biblioteca, con toda su técnica y nota de rodilla/menisco. */
data class Exercise(
    val id: String,
    val nombre: String,
    val subtitulo: String,
    val musculos: String,
    val bullets: List<String>,
    val tecnica: String,
    val rodilla: String,
    val foco: String,
    val seriesRecom: String,
    val categoria: String,
    val usaKettlebell: Boolean,
    /** id del ejercicio equivalente en la base pública free-exercise-db (dominio público),
     * usado para mostrar una foto real de miniatura. Null si no hay una coincidencia buena. */
    val imagenId: String? = null
) {
    /** URL de la foto real de la miniatura, o null si no hay coincidencia (se usa ícono de respaldo). */
    val imagenUrl: String?
        get() = imagenId?.let { "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$it/0.jpg" }
}

/** Una entrada dentro del plan de un día: referencia a un ejercicio + series/descanso de ese día puntual. */
data class PlanItem(
    val idLib: String,
    val series: String,
    val descanso: String,
    val tecnica: String,
    val rodilla: String
)

/** El plan de un día de la semana. */
data class DayPlan(
    val id: String,
    val dia: String,
    val titulo: String,
    val subtitulo: String,
    val duracion: String,
    val intensidad: String,
    val deporte: String?,
    val esDeporte: Boolean,
    val planNormal: List<PlanItem>,
    val planAlternativoId: String
)

/** Rutina alternativa para un día de deporte (ej: si no hay patinaje ese día). */
data class AltRoutine(
    val titulo: String,
    val subtitulo: String,
    val ejercicios: List<PlanItem>
)

/** Rotación de combinación bíceps/tríceps por semana del mes (1 a 4). */
data class BicepsTricepsCombo(
    val triceps: List<String>,
    val biceps: List<String>,
    val label: String
)
