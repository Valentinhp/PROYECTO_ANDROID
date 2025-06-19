// app/src/main/java/com/project/rc_mecha_maint/ui/mas/facturas/InvoiceViewModel.kt
package com.project.rc_mecha_maint.ui.mas.facturas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.repository.InvoiceRepository
import kotlinx.coroutines.launch

class InvoiceViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: InvoiceRepository

    init {
        val db = AppDatabase.getInstance(app)
        repo = InvoiceRepository(db.invoiceDao())
    }

    /** Devuelve las facturas asociadas a un historyId */
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>> =
        repo.getInvoicesByHistory(historyId)

    /** Inserta una nueva factura en background */
    fun insertInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repo.insert(invoice)
        }
    }

    /** Elimina una factura existente */
    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repo.delete(invoice)
        }
    }

    /** Actualiza solo el monto de una factura dada su id */
    fun updateInvoiceMonto(id: Long, monto: Double) {
        viewModelScope.launch {
            repo.updateMonto(id, monto)
        }
    }
}
