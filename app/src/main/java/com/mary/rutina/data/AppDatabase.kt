package com.mary.rutina.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackingEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackingDao(): TrackingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rutina_mary.db"
                )
                    // App en desarrollo: si cambia el esquema, se recrea la base en vez de migrar.
                    // Esto borra el historial guardado hasta ahora (aceptable en esta etapa de pruebas).
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
