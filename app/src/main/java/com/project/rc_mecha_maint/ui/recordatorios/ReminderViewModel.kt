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
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init {
        val dao = AppDatabase.getInstance(application).reminderDao()
        repository = ReminderRepository(dao)
        allReminders = repository.getAllReminders()
    }

    /**
     * Inserta un recordatorio y, usando el callback, devuelve el ID generado.
     * Luego programa la notificación usando ese ID.
     */
    fun insertReminder(rem: Reminder, onInserted: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) Insertar en BD y recibir el nuevo ID
            val newId: Long = repository.insertReminder(rem)
            // 2) Asignarlo al objeto (id es var en la entidad)
            rem.id = newId

            // 3) Programar notificación si corresponde
            if (rem.notificar) {
                scheduleNotification(rem)
            }

            // 4) Devolver el ID en el hilo Main
            withContext(Dispatchers.Main) {
                onInserted(newId)
            }
        }
    }

    /**
     * Actualiza un recordatorio y (re)programa o cancela su notificación.
     */
    fun updateReminder(rem: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(rem)
            if (rem.notificar) {
                scheduleNotification(rem)
            } else {
                cancelNotification(rem.id)
            }
        }
    }

    /**
     * Elimina un recordatorio y cancela su notificación.
     */
    fun deleteReminder(rem: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(rem)
            cancelNotification(rem.id)
        }
    }

    /**
     * Programa una notificación única para este Reminder usando WorkManager.
     */
    private fun scheduleNotification(rem: Reminder) {
        val delay = rem.fechaTimestamp - System.currentTimeMillis()
        if (delay <= 0) return

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

    /**
     * Cancela cualquier notificación pendiente para este ID.
     */
    private fun cancelNotification(reminderId: Long) {
        WorkManager.getInstance(getApplication())
            .cancelAllWorkByTag("reminder_$reminderId")
    }
}
