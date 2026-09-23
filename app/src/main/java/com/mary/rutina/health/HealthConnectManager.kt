package com.mary.rutina.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Estado de disponibilidad de Health Connect en este teléfono.
 */
enum class HealthConnectAvailability { DISPONIBLE, REQUIERE_ACTUALIZAR, NO_DISPONIBLE }

/**
 * Métricas de un día, leídas desde Health Connect (que a su vez recibe los datos
 * del Amazfit a través de la app Zepp / Mi Fitness). Cualquier campo null significa
 * que no había datos ese día en Health Connect.
 */
data class DailyHealthMetrics(
    val fcAvg: Int? = null,
    val fcMax: Int? = null,
    val pasos: Int? = null,
    val calorias: Int? = null,
    val suenoHoras: Double? = null,
    val minutosEjercicio: Int? = null,
    val distanciaKm: Double? = null
)

class HealthConnectManager(private val context: Context) {

    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
    )

    fun availability(): HealthConnectAvailability {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.DISPONIBLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.REQUIERE_ACTUALIZAR
            else -> HealthConnectAvailability.NO_DISPONIBLE
        }
    }

    private fun client(): HealthConnectClient = HealthConnectClient.getOrCreate(context)

    fun permissionRequestContract() = PermissionController.createRequestPermissionResultContract()

    suspend fun hasAllPermissions(): Boolean {
        val granted = client().permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    /** Lee las métricas de HOY (frecuencia cardíaca, pasos, calorías, sueño de anoche, ejercicio). */
    suspend fun readTodayMetrics(): DailyHealthMetrics {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startOfDay = today.atStartOfDay(zone).toInstant()
        val now = Instant.now()
        val timeRange = TimeRangeFilter.between(startOfDay, now)

        val hrRecords = client().readRecords(
            ReadRecordsRequest(HeartRateRecord::class, timeRange)
        ).records
        val bpmList = hrRecords.flatMap { it.samples }.map { it.beatsPerMinute }
        val fcAvg = if (bpmList.isNotEmpty()) bpmList.average().toInt() else null
        val fcMax = bpmList.maxOrNull()?.toInt()

        val stepsRecords = client().readRecords(
            ReadRecordsRequest(StepsRecord::class, timeRange)
        ).records
        val pasos = stepsRecords.sumOf { it.count }.toInt().takeIf { stepsRecords.isNotEmpty() }

        val calRecords = client().readRecords(
            ReadRecordsRequest(TotalCaloriesBurnedRecord::class, timeRange)
        ).records
        val calorias = calRecords.sumOf { it.energy.inKilocalories }.toInt().takeIf { calRecords.isNotEmpty() }

        // Sueño de anoche: ventana desde ayer 18:00 hasta ahora
        val sleepWindowStart = today.minusDays(1).atTime(18, 0).atZone(zone).toInstant()
        val sleepRecords = client().readRecords(
            ReadRecordsRequest(SleepSessionRecord::class, TimeRangeFilter.between(sleepWindowStart, now))
        ).records
        val suenoHoras = sleepRecords
            .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
            .takeIf { sleepRecords.isNotEmpty() }
            ?.let { it / 60.0 }

        val exerciseRecords = client().readRecords(
            ReadRecordsRequest(ExerciseSessionRecord::class, timeRange)
        ).records
        val minutosEjercicio = exerciseRecords
            .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
            .toInt().takeIf { exerciseRecords.isNotEmpty() }

        return DailyHealthMetrics(
            fcAvg = fcAvg,
            fcMax = fcMax,
            pasos = pasos,
            calorias = calorias,
            suenoHoras = suenoHoras,
            minutosEjercicio = minutosEjercicio,
            distanciaKm = null // Requiere DistanceRecord; se puede sumar igual que pasos si se necesita.
        )
    }

    /** Minutos totales de bici (estática o al aire libre) desde el lunes de esta semana hasta ahora. */
    suspend fun readWeeklyBikingMinutes(): Int {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        val start = monday.atStartOfDay(zone).toInstant()
        val now = Instant.now()

        val records = client().readRecords(
            ReadRecordsRequest(ExerciseSessionRecord::class, TimeRangeFilter.between(start, now))
        ).records

        val bikingTypes = setOf(
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY
        )

        return records
            .filter { it.exerciseType in bikingTypes }
            .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }
            .toInt()
    }
}
