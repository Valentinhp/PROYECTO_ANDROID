package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.*
import com.project.rc_mecha_maint.data.entity.*

/**
 * Base de datos principal con todas las entidades de la app:
 *  - Vehicle     → vehículos
 *  - Reminder    → recordatorios
 *  - History     → historial de acciones
 *  - Invoice     → facturas
 *  - Workshop    → talleres
 *  - Symptom     → síntomas (para diagnóstico)
 *  - Failure     → fallas (con síntomas asociados)
 *  - UserProfile → datos de usuario (perfil)
 *
 * Versiona la base de datos cada vez que añades o quitas entidades/DAOs.
 */
@Database(
    entities = [
        Vehicle::class,
        Reminder::class,
        History::class,
        Invoice::class,
        Workshop::class,
        Symptom::class,
        Failure::class,
        UserProfile::class
    ],
    version = 9,            // Sube a 10 si ya estabas en 9 antes de añadir estas tablas
    exportSchema = false    // No guarda un esquema JSON
)
abstract class AppDatabase : RoomDatabase() {

    // Cada DAO expone las operaciones CRUD de su entidad
    abstract fun vehicleDao(): VehicleDao
    abstract fun reminderDao(): ReminderDao
    abstract fun historyDao(): HistoryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun workshopDao(): WorkshopDao
    abstract fun symptomDao(): SymptomDao
    abstract fun failureDao(): FailureDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        // Instancia única para toda la app (singleton thread-safe)
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"               // Nombre del fichero .db
                )
                    .fallbackToDestructiveMigration()  // Borra todo y recrea si cambias versión
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
