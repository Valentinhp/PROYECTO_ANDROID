package com.project.rc_mecha_maint.ui.mas.facturas

// ui/facturas/InvoiceViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.repository.InvoiceRepository
import kotlinx.coroutines.launch

/**
 * InvoiceViewModel: conecta la UI de facturas con los datos.
 *
 * @param repository Repositorio para Invoice.
 */
class InvoiceViewModel(private val repository: InvoiceRepository) : ViewModel() {

    // 1) Obtener las facturas de un historial
    fun getInvoicesByHistory(historyId: Long): LiveData<List<Invoice>> {
        return repository.getInvoicesByHistory(historyId)
    }

    // 2) Insertar nueva factura
    fun insertInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.insertInvoice(invoice)
        }
    }

    // 3) Eliminar factura
    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }

    // 4) (Opcional) Reprocesar OCR: solo actualiza monto
    fun updateInvoiceMonto(id: Long, monto: Double) {
        viewModelScope.launch {
            repository.updateInvoiceMonto(id, monto)
        }
    }
}
