package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.rc_mecha_maint.data.entity.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY fechaTimestamp ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminder(reminder: Reminder): Long   // ← Ahora devuelve Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    /**
     * Obtener un solo Reminder por su ID.
     * Suspend para poder llamarlo desde un CoroutineWorker.
     */
    @Query("SELECT * FROM reminder_table WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
}
