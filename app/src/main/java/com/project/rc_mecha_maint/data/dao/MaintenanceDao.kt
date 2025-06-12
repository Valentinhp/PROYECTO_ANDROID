package com.project.rc_mecha_maint.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Maintenance

@Dao
interface MaintenanceDao {

    /**
     * Devuelve el próximo mantenimiento cuya fecha sea >= ahora,
     * ordenado ascendente y limitado a 1.
     */
    @Query("""
        SELECT * FROM maintenance_table
        WHERE fechaTimestamp >= :now
        ORDER BY fechaTimestamp ASC
        LIMIT 1
    """)
    suspend fun getNextMaintenance(now: Long): Maintenance?
}
