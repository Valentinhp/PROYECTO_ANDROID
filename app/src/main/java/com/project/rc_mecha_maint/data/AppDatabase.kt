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
 * Versión: 9 (sube +1 cada vez que cambias entidades o DAOs)
 */
@Database(
    entities = [
        Vehicle::class,
        Reminder::class,
        History::class,
        Invoice::class,
        Workshop::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun reminderDao(): ReminderDao
    abstract fun historyDao(): HistoryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun workshopDao(): WorkshopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene el singleton de la base de datos, thread-safe.
         * Si no existe, la construye con fallbackToDestructiveMigration().
         */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
