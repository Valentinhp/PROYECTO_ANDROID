package com.project.rc_mecha_maint.ui.recordatorios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.data.repository.ReminderRepository
import com.project.rc_mecha_maint.ui.worker.ReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel para manejar los recordatorios:
 * — Lista de LiveData
 * — Insertar, actualizar y eliminar en BD
 * — Programar/cancelar notificaciones con WorkManager
 */
class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init {
        val dao = AppDatabase.getInstance(application).reminderDao()
        repository = ReminderRepository(dao)
        allReminders = repository.getAllReminders()
    }

    /** Inserta un recordatorio y, si está marcado para notificar, lo programa. */
    fun insertReminder(rem: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertReminder(rem)
        if (rem.notificar) {
            scheduleNotification(rem)
        }
    }

    /** Actualiza un recordatorio y (re)programa o cancela la notificación. */
    fun updateReminder(rem: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateReminder(rem)
        if (rem.notificar) {
            scheduleNotification(rem)
        } else {
            cancelNotification(rem.id)
        }
    }

    /** Elimina un recordatorio y cancela su notificación si la tuviera. */
    fun deleteReminder(rem: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteReminder(rem)
        cancelNotification(rem.id)
    }

    /** Programa una sola notificación para este reminder. */
    private fun scheduleNotification(rem: Reminder) {
        val delay = rem.fechaTimestamp - System.currentTimeMillis()
        if (delay <= 0) return  // no programar si la fecha ya pasó

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_REMINDER_ID, rem.id)
            .build()

        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_${rem.id}")
            .build()

        WorkManager.getInstance(getApplication()).enqueue(work)
    }

    /** Cancela cualquier notificación pendiente para este ID. */
    private fun cancelNotification(reminderId: Long) {
        WorkManager.getInstance(getApplication())
            .cancelAllWorkByTag("reminder_$reminderId")
    }
}
