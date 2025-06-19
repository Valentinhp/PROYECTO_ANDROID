package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.entity.CategoriaTotal

@Dao
interface InvoiceDao {

    // 1) Obtener todas las facturas de un historial, ordenadas por fecha descendente.
    @Query("""
        SELECT * 
          FROM invoice_table 
         WHERE historyId = :historyId 
      ORDER BY fechaTimestamp DESC
    """)
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>>

    // 2) Insertar una nueva factura.
    @Insert
    suspend fun insert(invoice: Invoice)

    // 3) Eliminar una factura.
    @Delete
    suspend fun delete(invoice: Invoice)

    // 4) Actualizar solo el monto de una factura existente.
    @Query("UPDATE invoice_table SET monto = :monto WHERE id = :id")
    suspend fun updateMonto(id: Long, monto: Double)

    // 5) Contar cuántas facturas hay entre dos timestamps.
    @Query("""
        SELECT COUNT(*) 
          FROM invoice_table 
         WHERE fechaTimestamp BETWEEN :inicio AND :fin
    """)
    fun getInvoiceCountByDate(inicio: Long, fin: Long): LiveData<Int>

    // 6) Sumar todos los montos de las facturas.
    @Query("SELECT SUM(monto) FROM invoice_table")
    fun getTotalMonto(): LiveData<Double?>

    // 7) Calcular el promedio de los montos de todas las facturas.
    @Query("SELECT AVG(monto) FROM invoice_table")
    fun getAverageCost(): LiveData<Double?>

    // 8) Total gastado entre dos fechas (para Inicio o Reportes).
    @Query("""
        SELECT SUM(monto) 
          FROM invoice_table 
         WHERE fechaTimestamp BETWEEN :start AND :end
    """)
    fun getTotalSpent(start: Long, end: Long): LiveData<Double?>

    // 9) Distribución de gastos por categoría (para PieChart).
    @Query("""
        SELECT categoria AS categoria, 
               SUM(monto)   AS total
          FROM invoice_table
         WHERE fechaTimestamp BETWEEN :start AND :end
      GROUP BY categoria
    """)
    fun getTotalByCategory(start: Long, end: Long): LiveData<List<CategoriaTotal>>
}
