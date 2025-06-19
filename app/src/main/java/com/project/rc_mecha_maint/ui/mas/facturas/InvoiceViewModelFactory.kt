// app/src/main/java/com/project/rc_mecha_maint/ui/mas/facturas/InvoiceViewModelFactory.kt
package com.project.rc_mecha_maint.ui.mas.facturas

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para crear InvoiceViewModel pasándole el Application.
 */
class InvoiceViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InvoiceViewModel::class.java) ->
                InvoiceViewModel(application) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
