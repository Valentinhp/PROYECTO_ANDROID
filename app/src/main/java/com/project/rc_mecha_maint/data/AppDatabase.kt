package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.*    // Asegúrate de que MaintenanceDao esté aquí
import com.project.rc_mecha_maint.data.entity.* // Asegúrate de que Maintenance esté aquí

@Database(
    entities = [
        Vehicle::class,
        Reminder::class,
        History::class,
        Invoice::class,
        Workshop::class,
        Symptom::class,
        Failure::class,
        UserProfile::class,
        Maintenance::class      // <-- Nueva entidad
    ],
    version = 11,               // <-- Sube la versión (antes 9)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun reminderDao(): ReminderDao
    abstract fun historyDao(): HistoryDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun workshopDao(): WorkshopDao
    abstract fun symptomDao(): SymptomDao
    abstract fun failureDao(): FailureDao
    abstract fun userProfileDao(): UserProfileDao

    abstract fun maintenanceDao(): MaintenanceDao  // <-- Nuevo DAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
