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
    @Query(
        "SELECT * " +
                "FROM history_table " +
                "WHERE vehicleId = :vehicleId " +
                "ORDER BY fechaTimestamp DESC"
    )
    fun getHistoryByVehicle(vehicleId: Long): LiveData<List<History>>

    // 2) Insertar un nuevo registro de historial.
    @Insert
    suspend fun insert(history: History)

    // 3) Actualizar un registro de historial existente.
    @Update
    suspend fun update(history: History)

    // 4) Borrar un registro de historial.
    @Delete
    suspend fun delete(history: History)

    // 5) Calcular la suma de todos los costos entre dos timestamps:
    //    - inicio: marca de tiempo inicial (en milisegundos).
    //    - fin:   marca de tiempo final (en milisegundos).
    // Devuelve un LiveData que puede ser null si no hay registros en ese rango.
    @Query("""
        SELECT SUM(costo)
        FROM history_table
        WHERE fechaTimestamp BETWEEN :inicio AND :fin
    """)
    fun getTotalCostByDate(inicio: Long, fin: Long): LiveData<Double?>

    // 6) Obtener el costo promedio de todos los servicios registrados.
    //    Devuelve un LiveData que puede ser null si la tabla está vacía.
    @Query("SELECT AVG(costo) FROM history_table")
    fun getAverageCost(): LiveData<Double?>
}
