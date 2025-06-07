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
    //    historyId: id del historial al que pertenecen las facturas.
    @Query(
        "SELECT * " +
                "FROM invoice_table " +
                "WHERE historyId = :historyId " +
                "ORDER BY fechaTimestamp DESC"
    )
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>>

    // 2) Insertar una nueva factura.
    //    invoice: objeto Invoice con todos sus campos llenos.
    @Insert
    suspend fun insert(invoice: Invoice)

    // 3) Actualizar solo el monto de una factura existente.
    //    id: id de la factura a modificar.
    //    monto: nuevo valor de monto.
    @Query("UPDATE invoice_table SET monto = :monto WHERE id = :id")
    suspend fun updateMonto(id: Long, monto: Double)

    // 4) Eliminar una factura.
    //    invoice: objeto Invoice que se desea borrar.
    @Delete
    suspend fun delete(invoice: Invoice)

    // 5) Contar cuántas facturas hay entre dos timestamps.
    //    inicio: marca de tiempo inicial (en milisegundos).
    //    fin:   marca de tiempo final (en milisegundos).
    //    Devuelve un LiveData<Int> con el número de facturas.
    @Query("""
        SELECT COUNT(*) 
        FROM invoice_table 
        WHERE fechaTimestamp BETWEEN :inicio AND :fin
    """)
    fun getInvoiceCountByDate(inicio: Long, fin: Long): LiveData<Int>
}
