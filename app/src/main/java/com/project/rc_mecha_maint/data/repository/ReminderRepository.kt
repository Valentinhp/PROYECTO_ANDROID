package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.ReminderDao
import com.project.rc_mecha_maint.data.entity.Reminder

class ReminderRepository(private val dao: ReminderDao) {

    fun getAllReminders(): LiveData<List<Reminder>> =
        dao.getAllReminders()

    /**
     * Inserta un reminder y devuelve el ID generado.
     */
    suspend fun insertReminder(reminder: Reminder): Long {
        return dao.insertReminder(reminder)
    }

    /**
     * Actualiza un reminder existente.
     */
    suspend fun updateReminder(reminder: Reminder) {
        dao.update(reminder)
    }

    /**
     * Elimina un reminder.
     */
    suspend fun deleteReminder(reminder: Reminder) {
        dao.delete(reminder)
    }

    /**
     * Obtiene un reminder por su ID.
     */
    suspend fun getReminderById(id: Long): Reminder? =
        dao.getReminderById(id)
}
