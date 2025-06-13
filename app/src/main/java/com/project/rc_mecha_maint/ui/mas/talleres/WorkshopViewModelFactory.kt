package com.project.rc_mecha_maint.ui.mas.talleres

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WorkshopViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkshopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkshopViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
