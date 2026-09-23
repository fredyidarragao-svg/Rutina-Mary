package com.mary.rutina.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {

    @Query("SELECT * FROM tracking ORDER BY fecha DESC")
    fun observeAll(): Flow<List<TrackingEntity>>

    @Query("SELECT * FROM tracking WHERE fecha = :fecha LIMIT 1")
    suspend fun getByFecha(fecha: String): TrackingEntity?

    @Query("DELETE FROM tracking")
    suspend fun borrarTodo()

    @Query("SELECT * FROM tracking WHERE fecha BETWEEN :inicio AND :fin")
    suspend fun getEntreFechas(inicio: String, fin: String): List<TrackingEntity>

    @Query("SELECT * FROM tracking ORDER BY fecha DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<TrackingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: TrackingEntity)
}
