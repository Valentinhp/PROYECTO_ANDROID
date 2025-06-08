package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.rc_mecha_maint.data.entity.Workshop

@Dao
interface WorkshopDao {

    /** Lista completa, ordenada alfabéticamente. */
    @Query("SELECT * FROM workshop_table ORDER BY nombre")
    fun getAll(): LiveData<List<Workshop>>

    /** Inserta o reemplaza (para Alta y Edición). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workshop: Workshop)

    /** Borra un taller. */
    @Delete
    suspend fun delete(workshop: Workshop)

    /** Borra todos (evita duplicados al precargar). */
    @Query("DELETE FROM workshop_table")
    suspend fun clearTable()

    /** ¿Cuántos talleres hay? → para saber si ya está precargado. */
    @Query("SELECT COUNT(*) FROM workshop_table")
    suspend fun count(): Int
}
