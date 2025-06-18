package com.project.rc_mecha_maint.data.repository

import com.project.rc_mecha_maint.data.dao.AutoparteDao
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import kotlinx.coroutines.flow.Flow

// app/src/main/java/com/project/rc_mecha_maint/data/repository/AutoparteRepository.kt
class AutoparteRepository(private val dao: AutoparteDao) {
    fun buscarPiezas(filtro: String): Flow<List<AutoparteEntity>> =
        dao.buscar(filtro)

    suspend fun guardar(autoparte: AutoparteEntity) =
        dao.insert(autoparte)

    // --- Nuevo:
    fun getByClave(clave: String): Flow<List<AutoparteEntity>> =
        dao.getByClave(clave)
}
