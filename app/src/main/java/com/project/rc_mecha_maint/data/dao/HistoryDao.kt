// data/dao/HistoryDao.kt

package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.rc_mecha_maint.data.entity.History

@Dao
interface HistoryDao {

    // 1) Obtener todo el historial de un vehículo, ordenado por fecha descendente.
    @Query("SELECT * FROM history_table WHERE vehicleId = :vehicleId ORDER BY fechaTimestamp DESC")
    fun getHistoryByVehicle(vehicleId: Long): LiveData<List<History>>

    // 2) Insertar un nuevo registro de historial.
    @Insert
    suspend fun insert(history: History)

    // 3) Actualizar un registro existente.
    @Update
    suspend fun update(history: History)

    // 4) Borrar un registro de historial.
    @Delete
    suspend fun delete(history: History)
}
