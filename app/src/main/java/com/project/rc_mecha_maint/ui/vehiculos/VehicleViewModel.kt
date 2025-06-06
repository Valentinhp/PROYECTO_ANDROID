package com.project.rc_mecha_maint.ui.vehiculos

import android.app.Application
import androidx.lifecycle.*
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Vehicle
import com.project.rc_mecha_maint.data.repository.VehicleRepository
import kotlinx.coroutines.launch

/**
 * VehicleViewModel expone datos para la UI y ejecuta operaciones en background.
 * Hereda de AndroidViewModel para tener acceso al context en caso de necesitar la BD.
 */
class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    // Obtener el DAO y luego el repositorio
    private val repository: VehicleRepository

    // LiveData que contiene la lista de vehículos
    val allVehicles: LiveData<List<Vehicle>>

    init {
        // Instanciar la base de datos y el repositorio
        val vehicleDao = AppDatabase.getInstance(application).vehicleDao()
        repository = VehicleRepository(vehicleDao)
        allVehicles = repository.allVehicles
    }

    /**
     * Insertar un nuevo vehículo.
     * launch(LiveData + Room + coroutines) -> se ejecuta en hilo secundario.
     */
    fun insertVehicle(vehicle: Vehicle) = viewModelScope.launch {
        repository.insertVehicle(vehicle)
    }

    /**
     * Actualizar un vehículo existente.
     */
    fun updateVehicle(vehicle: Vehicle) = viewModelScope.launch {
        repository.updateVehicle(vehicle)
    }

    /**
     * Borrar un vehículo.
     */
    fun deleteVehicle(vehicle: Vehicle) = viewModelScope.launch {
        repository.deleteVehicle(vehicle)
    }
}

/**
 * Factory para crear VehicleViewModel con un parámetro (Application).
 * Esto es necesario porque el ViewModel necesita acceso al contexto para la BD.
 */
class VehicleViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
