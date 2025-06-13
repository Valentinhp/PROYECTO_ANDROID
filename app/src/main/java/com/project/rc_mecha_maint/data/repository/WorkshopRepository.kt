package com.project.rc_mecha_maint.data.repository

import com.project.rc_mecha_maint.data.dao.WorkshopDao
import com.project.rc_mecha_maint.data.entity.Workshop
import kotlinx.coroutines.flow.Flow

class WorkshopRepository(private val dao: WorkshopDao) {
    fun getAll(): Flow<List<Workshop>>          = dao.getAll()
    suspend fun insertAll(list: List<Workshop>) = dao.insertAll(list)
    suspend fun insert(workshop: Workshop)      = dao.insert(workshop)
    suspend fun delete(workshop: Workshop)      = dao.delete(workshop)
    suspend fun clearAll()                      = dao.clearAll()
    fun count(): Flow<Int>                      = dao.count()
}
