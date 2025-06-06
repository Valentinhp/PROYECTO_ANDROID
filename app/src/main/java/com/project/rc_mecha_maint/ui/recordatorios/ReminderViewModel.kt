package com.project.rc_mecha_maint.ui.recordatorios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.AppDatabase        // <–– Import correcto
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.data.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init {
        // Importante: llamamos a AppDatabase.getDatabase(application) para obtener la instancia
        val reminderDao = AppDatabase.getInstance(application).reminderDao()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.getAllReminders()
    }

    fun insertReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteReminder(reminder)
    }
}
