// ui/recordatorios/ReminderViewModelFactory.kt
package com.project.rc_mecha_maint.ui.recordatorios

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReminderViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(cls: Class<T>): T =
        when {
            cls.isAssignableFrom(ReminderViewModel::class.java) ->
                ReminderViewModel(app) as T
            else -> throw IllegalArgumentException("Unknown VM")
        }
}
