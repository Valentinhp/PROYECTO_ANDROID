package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.VehicleDao
import com.project.rc_mecha_maint.data.entity.Vehicle

/**
 * VehicleRepository sirve de intermediario entre el ViewModel y el DAO.
 * Aquí puedes combinar varios DAO o hacer lógica extra si la necesitas.
 */
class VehicleRepository(private val vehicleDao: VehicleDao) {

    // Exponer todos los vehículos como LiveData
    val allVehicles: LiveData<List<Vehicle>> = vehicleDao.getAllVehicles()

    // Insertar un vehículo (suspender porque se hace en background)
    suspend fun insertVehicle(vehicle: Vehicle) {
        vehicleDao.insert(vehicle)
    }

    // Actualizar un vehículo
    suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.update(vehicle)
    }

    // Borrar un vehículo
    suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.delete(vehicle)
    }
}
