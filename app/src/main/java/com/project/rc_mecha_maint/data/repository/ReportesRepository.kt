package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.HistoryDao
import com.project.rc_mecha_maint.data.dao.InvoiceDao
import com.project.rc_mecha_maint.data.entity.CategoriaTotal

class ReportesRepository(
    private val historyDao: HistoryDao,
    private val invoiceDao: InvoiceDao
) {
    // Historial + facturas monto
    fun getTotalCost(inicio: Long, fin: Long): LiveData<Double?> =
        historyDao.getTotalCostByDate(inicio, fin)

    // Historial por categoría (mantenimiento)
    fun getHistoryByCategory(start: Long, end: Long): LiveData<List<CategoriaTotal>> =
        historyDao.getTotalByServiceCategory(start, end)

    // Conteo de facturas
    fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> =
        invoiceDao.getInvoiceCountByDate(inicio, fin)

    // Total monto facturas
    fun getTotalMonto(): LiveData<Double?> =
        invoiceDao.getTotalMonto()

    // Promedio monto facturas
    fun getAverageCost(): LiveData<Double?> =
        invoiceDao.getAverageCost()

    // Facturas monto en rango
    fun getTotalSpent(start: Long, end: Long): LiveData<Double?> =
        invoiceDao.getTotalSpent(start, end)

    // Facturas por categoría
    fun getInvoiceByCategory(start: Long, end: Long): LiveData<List<CategoriaTotal>> =
        invoiceDao.getTotalByCategory(start, end)
}
