package com.project.rc_mecha_maint.data.repository


import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.HistoryDao
import com.project.rc_mecha_maint.data.dao.InvoiceDao

/**
 * Repo que expone los datos para reportes y costos.
 * Recibe los DAOs y simplemente delega las llamadas.
 */
class ReportesRepository(
    private val historyDao: HistoryDao,
    private val invoiceDao: InvoiceDao
) {

    // Total gastado entre inicio y fin
    fun getTotalCost(inicio: Long, fin: Long): LiveData<Double?> =
        historyDao.getTotalCostByDate(inicio, fin)

    // Cantidad de facturas entre inicio y fin
    fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> =
        invoiceDao.getInvoiceCountByDate(inicio, fin)

    // Promedio de costo por servicio (toda la tabla)
    fun getAverageCost(): LiveData<Double?> =
        historyDao.getAverageCost()
}
