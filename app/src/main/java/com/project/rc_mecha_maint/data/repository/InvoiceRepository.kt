// data/repository/InvoiceRepository.kt

package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.InvoiceDao
import com.project.rc_mecha_maint.data.entity.Invoice

/**
 * InvoiceRepository: agrupa las operaciones de la tabla invoice_table.
 */
class InvoiceRepository(private val dao: InvoiceDao) {

    // 1) Obtener facturas por historyId
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>> {
        return dao.getInvoicesByHistory(historyId)
    }

    // 2) Insertar factura
    suspend fun insertInvoice(invoice: Invoice) {
        dao.insert(invoice)
    }

    // 3) Eliminar factura
    suspend fun deleteInvoice(invoice: Invoice) {
        dao.delete(invoice)
    }

    // 4) (Opcional) Actualizar monto
    suspend fun updateInvoiceMonto(id: Long, monto: Double) {
        dao.updateMonto(id, monto)
    }
}
