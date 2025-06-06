package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.rc_mecha_maint.data.entity.Vehicle

/**
 * VehicleDao define las operaciones de base de datos para la entidad Vehicle.
 * Aquí pones las consultas para leer, insertar, actualizar y borrar.
 */
@Dao
interface VehicleDao {

    // Obtener todos los vehículos como LiveData para que se actualice la UI automáticamente
    @Query("SELECT * FROM vehicles ORDER BY id DESC")
    fun getAllVehicles(): LiveData<List<Vehicle>>

    // Insertar un vehículo. Si hay conflicto (mismo id), reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    // Actualizar un vehículo existente
    @Update
    suspend fun update(vehicle: Vehicle)

    // Borrar un vehículo concreto
    @Delete
    suspend fun delete(vehicle: Vehicle)
}
