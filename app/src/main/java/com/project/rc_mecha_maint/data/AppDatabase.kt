package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.VehicleDao
import com.project.rc_mecha_maint.data.dao.ReminderDao
import com.project.rc_mecha_maint.data.dao.HistoryDao
import com.project.rc_mecha_maint.data.dao.InvoiceDao
import com.project.rc_mecha_maint.data.entity.Vehicle
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.data.entity.History
import com.project.rc_mecha_maint.data.entity.Invoice

/**
 * AppDatabase: base de datos de Room que ahora incluye:
 *  - Vehicle (vehículos)
 *  - Reminder (recordatorios)
 *  - History (historial)
 *  - Invoice (facturas)
 *
 * Cada DAO expone las operaciones CRUD para su tabla.
 *
 * Versión: 3  (se aumentó porque ya existía la versión 2 con Vehicle y Reminder)
 */
@Database(
    entities = [
        Vehicle::class,
        Reminder::class,
        History::class,
        Invoice::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // 1) DAO para operaciones sobre la tabla de vehículos
    abstract fun vehicleDao(): VehicleDao

    // 2) DAO para operaciones sobre la tabla de recordatorios
    abstract fun reminderDao(): ReminderDao

    // 3) DAO para operaciones sobre la tabla de historial
    abstract fun historyDao(): HistoryDao

    // 4) DAO para operaciones sobre la tabla de facturas
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        // Nombre del archivo de la base de datos
        private const val DB_NAME = "app_database.db"

        // Instancia única (singleton) de la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtener la instancia de la base de datos.
         * Si ya existe, la devuelve; si no, la crea.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    // Si cambia la versión (por ejemplo: 2 → 3), destruye y vuelve a crear
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
