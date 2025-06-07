package com.project.rc_mecha_maint.ui.reportes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.ReportesRepository

/**
 * ViewModel para reportes y costos.
 * Carga el repositorio y ofrece métodos sencillos.
 */
class ReportesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: ReportesRepository

    init {
        val db = AppDatabase.getInstance(app)
        repo = ReportesRepository(db.historyDao(), db.invoiceDao())
    }

    fun getTotalCost(inicio: Long, fin: Long): LiveData<Double?> =
        repo.getTotalCost(inicio, fin)

    fun getInvoiceCount(inicio: Long, fin: Long): LiveData<Int> =
        repo.getInvoiceCount(inicio, fin)

    fun getAverageCost(): LiveData<Double?> =
        repo.getAverageCost()
}
