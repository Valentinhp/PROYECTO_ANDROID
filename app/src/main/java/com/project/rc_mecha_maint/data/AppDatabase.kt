package com.project.rc_mecha_maint.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.rc_mecha_maint.data.dao.VehicleDao
import com.project.rc_mecha_maint.data.entity.Vehicle

/**
 * Clase abstracta que define la base de datos Room.
 * Indica la(s) entidad(es) y la versión. También expone el DAO.
 */
@Database(entities = [Vehicle::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Método para obtener el DAO de vehículos
    abstract fun vehicleDao(): VehicleDao

    companion object {
        // Nombre del archivo de base de datos
        private const val DB_NAME = "app_database.db"

        // Instancia única (singleton) de la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtener la instancia de la base de datos.
         * Si ya existe, la usa; si no, la crea.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    // En caso de cambio de versión, destruye y vuelve a crear.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
