package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.Failure

/**
 * DAO para acceder a las fallas.
 */
@Dao
interface FailureDao {
    @Query("SELECT * FROM failure_table")
    fun getAllFailures(): LiveData<List<Failure>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Failure>)
}
