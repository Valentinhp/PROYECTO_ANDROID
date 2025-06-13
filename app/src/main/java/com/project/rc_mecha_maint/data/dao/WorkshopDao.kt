package com.project.rc_mecha_maint.data.dao

import androidx.room.*
import com.project.rc_mecha_maint.data.entity.Workshop
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkshopDao {
    @Query("SELECT * FROM workshop_table")
    fun getAll(): Flow<List<Workshop>>

    @Query("SELECT * FROM workshop_table WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Workshop?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Workshop>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workshop: Workshop)

    @Delete
    suspend fun delete(workshop: Workshop)

    @Query("DELETE FROM workshop_table")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM workshop_table")
    fun count(): Flow<Int>

}
