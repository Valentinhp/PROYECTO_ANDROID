package com.project.rc_mecha_maint.data.repository

import com.project.rc_mecha_maint.data.dao.WorkshopDao
import com.project.rc_mecha_maint.data.entity.Workshop

class WorkshopRepository(private val dao: WorkshopDao) {

    /** LiveData que observa todos los talleres. */
    val workshops = dao.getAll()

    /** Alta / Edición. */
    suspend fun save(workshop: Workshop) = dao.insert(workshop)

    /** Borrado. */
    suspend fun delete(workshop: Workshop) = dao.delete(workshop)

    /** Precarga inicial desde JSON. */
    suspend fun precargar(lista: List<Workshop>) {
        dao.clearTable()
        lista.forEach { dao.insert(it) }
    }

    /** ¿Ya hay datos? */
    suspend fun yaHayDatos(): Boolean = dao.count() > 0
}
