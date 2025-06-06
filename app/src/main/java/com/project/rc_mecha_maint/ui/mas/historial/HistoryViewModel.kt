package com.project.rc_mecha_maint.ui.mas.historial


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.entity.History
import com.project.rc_mecha_maint.data.repository.HistoryRepository
import kotlinx.coroutines.launch

/**
 * HistoryViewModel: conecta la UI del historial con los datos
 * usando LiveData y corrutinas.
 *
 * @param repository Repositorio para acceder a los datos de History.
 */
class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    // 1) Obtener el historial de un vehículo
    fun getHistoryByVehicle(vehicleId: Long): LiveData<List<History>> {
        return repository.getHistoryByVehicle(vehicleId)
    }

    // 2) Insertar un nuevo registro (ejecutado en un hilo de fondo)
    fun insertHistory(history: History) {
        viewModelScope.launch {
            repository.insertHistory(history)
        }
    }

    // 3) Actualizar un registro
    fun updateHistory(history: History) {
        viewModelScope.launch {
            repository.updateHistory(history)
        }
    }

    // 4) Eliminar un registro
    fun deleteHistory(history: History) {
        viewModelScope.launch {
            repository.deleteHistory(history)
        }
    }
}
