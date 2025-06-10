package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Symptom

/**
 * DAO para acceder a los síntomas.
 */
@Dao
interface SymptomDao {
    @Query("SELECT * FROM symptom_table")
    fun getAllSymptoms(): LiveData<List<Symptom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Symptom>)
}
