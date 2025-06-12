package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Invoice

@Dao
interface InvoiceDao {

    // 1) Obtener todas las facturas de un historial, ordenadas por fecha descendente.
    @Query(
        "SELECT * " +
                "FROM invoice_table " +
                "WHERE historyId = :historyId " +
                "ORDER BY fechaTimestamp DESC"
    )
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>>

    // 2) Insertar una nueva factura.
    @Insert
    suspend fun insert(invoice: Invoice)

    // 3) Actualizar solo el monto de una factura existente.
    @Query("UPDATE invoice_table SET monto = :monto WHERE id = :id")
    suspend fun updateMonto(id: Long, monto: Double)

    // 4) Eliminar una factura.
    @Delete
    suspend fun delete(invoice: Invoice)

    // 5) Contar cuántas facturas hay entre dos timestamps.
    @Query("""
        SELECT COUNT(*) 
        FROM invoice_table 
        WHERE fechaTimestamp BETWEEN :inicio AND :fin
    """)
    fun getInvoiceCountByDate(inicio: Long, fin: Long): LiveData<Int>

    // 6) Sumar todos los montos de las facturas.
    //    Devuelve un LiveData<Double?> con la suma (null si no hay registros).
    @Query("SELECT SUM(monto) FROM invoice_table")
    fun getTotalMonto(): LiveData<Double?>

    // 7) Calcular el promedio de los montos de todas las facturas.
    //    Devuelve un LiveData<Double?> con el promedio (null si no hay registros).
    @Query("SELECT AVG(monto) FROM invoice_table")
    fun getAverageCost(): LiveData<Double?>


    /**
     * Suma todos los montos entre start y end
     * Devuelve null si no hay facturas
     */
    @Query("""
      SELECT SUM(monto) 
      FROM invoice_table 
      WHERE fechaTimestamp BETWEEN :start AND :end
    """)
    suspend fun getTotalSpent(start: Long, end: Long): Double?
}
