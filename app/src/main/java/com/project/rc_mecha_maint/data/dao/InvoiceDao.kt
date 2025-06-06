// data/dao/InvoiceDao.kt

package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Invoice

@Dao
interface InvoiceDao {

    // 1) Obtener todas las facturas de un historial, ordenadas por fecha descendente
    @Query("SELECT * FROM invoice_table WHERE historyId = :historyId ORDER BY fechaTimestamp DESC")
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>>

    // 2) Insertar una nueva factura
    @Insert
    suspend fun insert(invoice: Invoice)

    // 3) (Opcional) Actualizar una factura; si no la vas a editar, podrías quitarlo
    @Query("UPDATE invoice_table SET monto = :monto WHERE id = :id")
    suspend fun updateMonto(id: Long, monto: Double)

    // 4) Eliminar una factura
    @Delete
    suspend fun delete(invoice: Invoice)
}
