// data/repository/HistoryRepository.kt

package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.HistoryDao
import com.project.rc_mecha_maint.data.entity.History

/**
 * HistoryRepository: agrupa todas las operaciones con historial
 * para mantener el ViewModel limpio.
 */
class HistoryRepository(private val dao: HistoryDao) {

    // 1) Método para obtener el historial de un vehículo como LiveData
    fun getHistoryByVehicle(vehicleId: Long): LiveData<List<History>> {
        return dao.getHistoryByVehicle(vehicleId)
    }

    // 2) Insertar un nuevo registro (corrutina)
    suspend fun insertHistory(history: History) {
        dao.insert(history)
    }

    // 3) Actualizar un registro (corrutina)
    suspend fun updateHistory(history: History) {
        dao.update(history)
    }

    // 4) Eliminar un registro (corrutina)
    suspend fun deleteHistory(history: History) {
        dao.delete(history)
    }
}
