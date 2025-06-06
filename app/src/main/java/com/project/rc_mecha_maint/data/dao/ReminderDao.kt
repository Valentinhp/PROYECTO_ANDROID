package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.rc_mecha_maint.data.entity.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY fechaTimestamp ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    /**
     * NUEVO: obtener un solo Reminder por su ID.
     * Lo marcamos como suspend para poder llamarlo desde un CoroutineWorker.
     */
    @Query("SELECT * FROM reminder_table WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
}
