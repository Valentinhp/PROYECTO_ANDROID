package com.project.rc_mecha_maint.ui.reportes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.ReportesRepository

/**
 * ViewModel para reportes y costos.
 * Ahora combina la suma de History + Invoice en un solo LiveData.
 */
class ReportesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: ReportesRepository

    init {
        val db = AppDatabase.getInstance(app)
        repo = ReportesRepository(db.historyDao(), db.invoiceDao())
    }

    /**
     * 1) Suma el costo de History entre dos timestamps (inicio, fin).
     * 2) Suma todos los montos de Invoice.
     * 3) Devuelve un LiveData<Double> que combina ambas sumas.
     *
     * inicio, fin: milisegundos UTC.
     */
    fun getTotalCost(inicio: Long, fin: Long): LiveData<Double> {
        // LiveData con la suma de History
        val historySum: LiveData<Double?> = repo.getTotalCost(inicio, fin)
        // LiveData con la suma de todos los Invoice
        val invoiceSum: LiveData<Double?> = repo.getTotalMonto()

        // MediatorLiveData para emitir (historySum + invoiceSum)
        val combined = MediatorLiveData<Double>()

        // Cuando cambie historySum, recalcular
        combined.addSource(historySum) { h ->
            val inv = invoiceSum.value ?: 0.0
            combined.value = (h ?: 0.0) + inv
        }

        // Cuando cambie invoiceSum, recalcular
        combined.addSource(invoiceSum) { inv ->
            val hist = historySum.value ?: 0.0
            combined.value = hist + (inv ?: 0.0)
        }

        return combined
    }

    /**
     * Cuenta cuántas facturas hay entre dos fechas.
     */
    fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> =
        repo.getInvoiceCount(inicio, fin)

    /**
     * Calcula el costo promedio de las facturas.
     */
    fun getAverageCost(): LiveData<Double?> =
        repo.getAverageCost()
}
