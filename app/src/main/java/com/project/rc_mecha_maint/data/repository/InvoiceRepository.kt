package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.InvoiceDao
import com.project.rc_mecha_maint.data.entity.Invoice

/**
 * Repo para operaciones de factura: delega directamente en InvoiceDao.
 */
class InvoiceRepository(
    private val invoiceDao: InvoiceDao
) {
    /** 1) Obtener las facturas de un historial, ordenadas por fecha descendente */
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>> =
        invoiceDao.getInvoicesByHistory(historyId)

    /** 2) Insertar una nueva factura */
    suspend fun insert(invoice: Invoice) =
        invoiceDao.insert(invoice)

    /** 3) Eliminar una factura existente */
    suspend fun delete(invoice: Invoice) =
        invoiceDao.delete(invoice)

    /** 4) Actualizar el monto de una factura */
    suspend fun updateMonto(id: Long, monto: Double) =
        invoiceDao.updateMonto(id, monto)

    // Si en tu FragmentSubirFactura necesitas usar getInvoiceCountByDate, getTotalSpent, etc.,
    // agrégalos aquí de la misma forma:
    // fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> = invoiceDao.getInvoiceCountByDate(inicio, fin)
    // fun getTotalSpent(start: Long, end: Long): LiveData<Double?> = invoiceDao.getTotalSpent(start, end)
}
