package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.*
import com.project.rc_mecha_maint.data.entity.*

/**
 * AppDatabase: base de datos de Room que ahora incluye:
 *  - Vehicle (vehículos)
 *  - Reminder (recordatorios)
 *  - History (historial)
 *  - Invoice (facturas)
 *  - Workshop (talleres)
 *
 * Cada DAO expone las operaciones CRUD para su tabla.
 *
 * Versión: 4 (se subió porque se agregó Workshop)
 */
@Database(
    entities = [
        Vehicle::class,
        Reminder::class,
        History::class,
        Invoice::class,
        Workshop::class // <-- NUEVA entidad agregada
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // 1) DAO para vehículos
    abstract fun vehicleDao(): VehicleDao

    // 2) DAO para recordatorios
    abstract fun reminderDao(): ReminderDao

    // 3) DAO para historial
    abstract fun historyDao(): HistoryDao

    // 4) DAO para facturas
    abstract fun invoiceDao(): InvoiceDao

    // 5) DAO para talleres
    abstract fun workshopDao(): WorkshopDao  // <-- NUEVO DAO agregado

    companion object {
        private const val DB_NAME = "app_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration() // borra y recrea si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
