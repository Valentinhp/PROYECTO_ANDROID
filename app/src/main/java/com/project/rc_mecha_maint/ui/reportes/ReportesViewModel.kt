package com.project.rc_mecha_maint.ui.reportes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.CategoriaTotal
import com.project.rc_mecha_maint.data.repository.ReportesRepository

class ReportesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: ReportesRepository

    init {
        val db = AppDatabase.getInstance(app)
        repo = ReportesRepository(db.historyDao(), db.invoiceDao())
    }

    /** Historial + facturas en rango [inicio, fin]. */
    fun getTotalCost(inicio: Long, fin: Long): LiveData<Double> {
        val historySum = repo.getTotalCost(inicio, fin)
        val invoiceSum = repo.getTotalSpent(inicio, fin)
        val combined = MediatorLiveData<Double>()
        combined.addSource(historySum) { h ->
            combined.value = (h ?: 0.0) + (invoiceSum.value ?: 0.0)
        }
        combined.addSource(invoiceSum) { inv ->
            combined.value = (historySum.value ?: 0.0) + (inv ?: 0.0)
        }
        return combined
    }

    /** Solo historial monto. */
    fun getHistoryCost(start: Long, end: Long): LiveData<Double?> =
        repo.getTotalCost(start, end)

    /** Solo facturas monto. */
    fun getInvoiceCost(start: Long, end: Long): LiveData<Double?> =
        repo.getTotalSpent(start, end)

    /** Alias para compatibilidad. */
    fun getTotalSpent(start: Long, end: Long): LiveData<Double?> =
        getInvoiceCost(start, end)

    /** Conteo de facturas. */
    fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> =
        repo.getInvoiceCount(inicio, fin)

    /** Distribución combinada por categoría. */
    fun getCombinedByCategory(start: Long, end: Long): LiveData<List<CategoriaTotal>> {
        val historyCats = repo.getHistoryByCategory(start, end)
        val invoiceCats = repo.getInvoiceByCategory(start, end)
        val combined = MediatorLiveData<List<CategoriaTotal>>()

        fun merge(h: List<CategoriaTotal>?, i: List<CategoriaTotal>?) {
            val map = mutableMapOf<String, Double>()
            (h ?: emptyList()).forEach { map[it.categoria] = (map[it.categoria] ?: 0.0) + (it.total ?: 0.0) }
            (i ?: emptyList()).forEach { map[it.categoria] = (map[it.categoria] ?: 0.0) + (it.total ?: 0.0) }
            combined.value = map.map { CategoriaTotal(it.key, it.value) }
        }

        combined.addSource(historyCats) { h -> merge(h, invoiceCats.value) }
        combined.addSource(invoiceCats) { i -> merge(historyCats.value, i) }

        return combined
    }

    /** Costo medio de facturas. */
    fun getAverageCost(): LiveData<Double?> =
        repo.getAverageCost()
}
