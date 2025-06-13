package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.*
import com.project.rc_mecha_maint.data.entity.*

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
        Maintenance::class,
        AutoparteEntity::class  // ✅ Incluida la tabla de autopartes
    ],
    version = 15,    // ✅ Subida de versión para migraciones
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
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun autoparteDao(): AutoparteDao  // ✅ DAO para AutoparteEntity

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration()  // Destruye y recrea DB si cambia versión
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
