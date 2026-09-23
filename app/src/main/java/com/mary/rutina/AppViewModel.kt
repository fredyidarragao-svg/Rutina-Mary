package com.mary.rutina

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mary.rutina.data.AppDatabase
import com.mary.rutina.data.PlanItem
import com.mary.rutina.data.RoutineData
import com.mary.rutina.data.TrackingEntity
import com.mary.rutina.health.DailyHealthMetrics
import com.mary.rutina.health.HealthConnectAvailability
import com.mary.rutina.health.HealthConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.trackingDao()
    val healthConnect = HealthConnectManager(application)

    private val fechaFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // id de día -> ¿usando rutina ALT?
    private val _altToggles = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val altToggles: StateFlow<Map<String, Boolean>> = _altToggles.asStateFlow()

    private val _libraryFilter = MutableStateFlow("TODOS")
    val libraryFilter: StateFlow<String> = _libraryFilter.asStateFlow()

    private val _todayTracking = MutableStateFlow<TrackingEntity?>(null)
    val todayTracking: StateFlow<TrackingEntity?> = _todayTracking.asStateFlow()

    private val _healthAvailability = MutableStateFlow(HealthConnectAvailability.NO_DISPONIBLE)
    val healthAvailability: StateFlow<HealthConnectAvailability> = _healthAvailability.asStateFlow()

    private val _hasHealthPermissions = MutableStateFlow(false)
    val hasHealthPermissions: StateFlow<Boolean> = _hasHealthPermissions.asStateFlow()

    /** true si la FC promedio de las últimas sesiones registradas viene alta (posible fatiga). */
    private val _fatigaAlta = MutableStateFlow(false)
    val fatigaAlta: StateFlow<Boolean> = _fatigaAlta.asStateFlow()

    /** Semana de rotación 1..4 para pecho/bíceps/tríceps/core, basada en la semana del año. */
    val rotationWeek: Int
        get() {
            val weekOfYear = LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear())
            return ((weekOfYear - 1) % 4) + 1
        }

    init {
        _healthAvailability.value = healthConnect.availability()
        loadTodayTracking()
        evaluarFatiga()
        viewModelScope.launch {
            _hasHealthPermissions.value = runCatching { healthConnect.hasAllPermissions() }.getOrDefault(false)
        }
    }

    fun toggleAlt(dayId: String) {
        _altToggles.value = _altToggles.value.toMutableMap().apply {
            this[dayId] = !(this[dayId] ?: false)
        }
    }

    fun setLibraryFilter(categoria: String) {
        _libraryFilter.value = categoria
    }

    /** Resuelve los slots de rotación (ROT_*) al ejercicio real según la semana actual. */
    private fun resolveIfRotating(item: PlanItem): PlanItem {
        if (!item.idLib.startsWith("ROT_")) return item
        val resolvedId = when (item.idLib) {
            "ROT_GLUTEO" -> RoutineData.gluteoRotation[rotationWeek] ?: "hip-thrust-barra"
            "ROT_ABDOMEN" -> RoutineData.abdomenRotation[rotationWeek] ?: "rueda"
            "ROT_BRAZO1" -> RoutineData.brazoRotation[rotationWeek]?.triceps?.getOrNull(0) ?: "extension-triceps-ligero"
            "ROT_BRAZO2" -> RoutineData.brazoRotation[rotationWeek]?.biceps?.getOrNull(0) ?: "curl-biceps-ligero-manc"
            else -> item.idLib
        }
        val ex = RoutineData.exerciseById(resolvedId)
        return item.copy(
            idLib = resolvedId,
            tecnica = ex?.tecnica ?: item.tecnica,
            rodilla = ex?.rodilla ?: item.rodilla
        )
    }

    fun activePlanFor(dayId: String): List<PlanItem> {
        val day = RoutineData.weeklyPlan.find { it.id == dayId } ?: return emptyList()
        val usaAlt = _altToggles.value[dayId] == true
        val base = if (day.esDeporte && usaAlt) {
            RoutineData.altRoutines[day.planAlternativoId]?.ejercicios ?: day.planNormal
        } else {
            day.planNormal
        }
        return base.map { resolveIfRotating(it) }
    }

    fun activeTitleFor(dayId: String): String {
        val day = RoutineData.weeklyPlan.find { it.id == dayId } ?: return ""
        val usaAlt = _altToggles.value[dayId] == true
        return if (day.esDeporte && usaAlt) {
            RoutineData.altRoutines[day.planAlternativoId]?.titulo ?: (day.titulo + " ALT")
        } else {
            day.titulo
        }
    }

    private fun loadTodayTracking() {
        viewModelScope.launch {
            val fecha = LocalDate.now().format(fechaFormatter)
            _todayTracking.value = dao.getByFecha(fecha)
        }
    }

    /** Índices (dentro del plan activo de hoy) que ya se marcaron "Realizado". */
    fun completedIndices(): Set<Int> {
        val raw = _todayTracking.value?.completados ?: ""
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    /** Marca/desmarca un ejercicio puntual (extra o rutina ALT) como realizado. */
    fun toggleExerciseDone(dayId: String, index: Int, totalItems: Int, requiereActividad: Boolean) {
        viewModelScope.launch {
            val fecha = LocalDate.now().format(fechaFormatter)
            val existente = dao.getByFecha(fecha) ?: TrackingEntity(fecha = fecha, diaId = dayId)
            val set = existente.completados.split(",").mapNotNull { it.trim().toIntOrNull() }.toMutableSet()
            if (set.contains(index)) set.remove(index) else set.add(index)
            val extrasCompletos = totalItems == 0 || set.size >= totalItems
            val hecho = if (requiereActividad) existente.actividadHecha && extrasCompletos else extrasCompletos
            val actualizado = existente.copy(
                diaId = dayId,
                completados = set.sorted().joinToString(","),
                hechoHoy = hecho,
                usaAlt = _altToggles.value[dayId] == true
            )
            dao.upsert(actualizado)
            _todayTracking.value = actualizado
        }
    }

    /** Marca/desmarca la actividad principal (patinaje/fútbol/natación) como realizada hoy. */
    fun toggleActividadHecha(dayId: String, totalExtras: Int) {
        viewModelScope.launch {
            val fecha = LocalDate.now().format(fechaFormatter)
            val existente = dao.getByFecha(fecha) ?: TrackingEntity(fecha = fecha, diaId = dayId)
            val nuevaActividad = !existente.actividadHecha
            val extrasCompletos = totalExtras == 0 ||
                existente.completados.split(",").mapNotNull { it.trim().toIntOrNull() }.size >= totalExtras
            val actualizado = existente.copy(
                diaId = dayId,
                actividadHecha = nuevaActividad,
                hechoHoy = nuevaActividad && extrasCompletos,
                usaAlt = _altToggles.value[dayId] == true
            )
            dao.upsert(actualizado)
            _todayTracking.value = actualizado
        }
    }

    fun marcarHechoHoy(dayId: String, hecho: Boolean) {
        viewModelScope.launch {
            val fecha = LocalDate.now().format(fechaFormatter)
            val existente = dao.getByFecha(fecha)
            val actualizado = (existente ?: TrackingEntity(fecha = fecha, diaId = dayId))
                .copy(hechoHoy = hecho, usaAlt = _altToggles.value[dayId] == true)
            dao.upsert(actualizado)
            _todayTracking.value = actualizado
        }
    }

    fun guardarMetricas(
        dayId: String,
        fcAvg: Int?,
        fcMax: Int?,
        fcReposo: Int?,
        calorias: Int?,
        tiempoMin: Int?,
        distanciaKm: Double?,
        suenoHoras: Double?,
        pasos: Int?,
        fuenteAuto: Boolean,
        sinActividad: Boolean = false
    ) {
        viewModelScope.launch {
            val fecha = LocalDate.now().format(fechaFormatter)
            val existente = dao.getByFecha(fecha)
            val actualizado = (existente ?: TrackingEntity(fecha = fecha, diaId = dayId)).copy(
                fcAvg = fcAvg, fcMax = fcMax, fcReposo = fcReposo, calorias = calorias, tiempoMin = tiempoMin,
                distanciaKm = distanciaKm, suenoHoras = suenoHoras, pasos = pasos, fuenteAuto = fuenteAuto,
                sinActividad = sinActividad
            )
            dao.upsert(actualizado)
            _todayTracking.value = actualizado
            evaluarFatiga()
        }
    }

    /** id del día real de hoy (lun..dom) según la fecha del sistema, para registrar sin entrar a un día. */
    fun todayDayId(): String {
        return when (LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "lun"
            java.time.DayOfWeek.TUESDAY -> "mar"
            java.time.DayOfWeek.WEDNESDAY -> "mie"
            java.time.DayOfWeek.THURSDAY -> "jue"
            java.time.DayOfWeek.FRIDAY -> "vie"
            java.time.DayOfWeek.SATURDAY -> "sab"
            java.time.DayOfWeek.SUNDAY -> "dom"
        }
    }

    /** Revisa las últimas sesiones con FC en reposo registrada (más confiable que la FC de entrenamiento);
     * si viene consistentemente alta respecto al resto, sugiere bajar intensidad. Si no hay suficientes
     * lecturas de FC en reposo, usa la FC promedio de entrenamiento como respaldo. */
    private fun evaluarFatiga() {
        viewModelScope.launch {
            val recientes = dao.getRecent(6)
            val reposo = recientes.mapNotNull { it.fcReposo }
            if (reposo.size >= 3) {
                val ultima = reposo.first()
                val base = reposo.drop(1).average()
                _fatigaAlta.value = ultima > base + 6.0
            } else {
                val entrenamiento = recientes.mapNotNull { it.fcAvg }
                _fatigaAlta.value = entrenamiento.size >= 2 && entrenamiento.take(3).average() > 155.0
            }
        }
    }

    private val _historialMes = MutableStateFlow<Map<String, TrackingEntity>>(emptyMap())
    val historialMes: StateFlow<Map<String, TrackingEntity>> = _historialMes.asStateFlow()

    /** Carga el historial de un mes (yyyy-MM) para pintar el calendario. */
    fun cargarHistorialMes(yearMonth: java.time.YearMonth) {
        viewModelScope.launch {
            val inicio = yearMonth.atDay(1).format(fechaFormatter)
            val fin = yearMonth.atEndOfMonth().format(fechaFormatter)
            _historialMes.value = dao.getEntreFechas(inicio, fin).associateBy { it.fecha }
        }
    }

    /** Borra por completo el historial guardado (todos los registros de todos los días). */
    fun borrarHistorialCompleto() {
        viewModelScope.launch {
            dao.borrarTodo()
            _todayTracking.value = null
            _historialMes.value = emptyMap()
            _fatigaAlta.value = false
        }
    }

    fun refreshHealthPermissions() {
        viewModelScope.launch {
            _hasHealthPermissions.value = runCatching { healthConnect.hasAllPermissions() }.getOrDefault(false)
        }
    }

    suspend fun leerMetricasDeHoyDesdeReloj(): DailyHealthMetrics = healthConnect.readTodayMetrics()
}
