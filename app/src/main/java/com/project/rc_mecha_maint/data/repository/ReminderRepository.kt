// data/repository/ReminderRepository.kt

package com.project.rc_mecha_maint.data.repository

import androidx.lifecycle.LiveData
import com.project.rc_mecha_maint.data.dao.ReminderDao
import com.project.rc_mecha_maint.data.entity.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    // Exponemos LiveData para la lista de recordatorios
    fun getAllReminders(): LiveData<List<Reminder>> {
        return reminderDao.getAllReminders()
    }

    // Las siguientes funciones son suspend para usarse en corrutinas
    suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}
