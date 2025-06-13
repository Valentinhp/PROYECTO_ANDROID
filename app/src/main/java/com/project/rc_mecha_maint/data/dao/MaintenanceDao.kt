package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Maintenance

@Dao
interface MaintenanceDao {

    /**
     * Inserta un nuevo mantenimiento.
     */
    @Insert
    suspend fun insert(mantenimiento: Maintenance)

    /**
     * Devuelve el próximo mantenimiento cuya fecha sea mayor o igual a ahora.
     */
    @Query("""
        SELECT * FROM maintenance_table
        WHERE fechaTimestamp >= :now
        ORDER BY fechaTimestamp ASC
        LIMIT 1
    """)
    suspend fun getNextMaintenance(now: Long): Maintenance?

    /**
     * Devuelve todo el historial de mantenimiento de un vehículo específico,
     * ordenado de más reciente a más antiguo.
     */
    @Query("""
        SELECT * FROM maintenance_table
        WHERE vehicleId = :vehicleId
        ORDER BY fechaTimestamp DESC
    """)
    fun getHistoryByVehicle(vehicleId: Long): LiveData<List<Maintenance>>
}
