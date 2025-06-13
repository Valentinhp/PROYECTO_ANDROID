package com.project.rc_mecha_maint.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoparteDao {
    @Query("SELECT * FROM autoparte_table WHERE descripcion LIKE '%' || :filtro || '%'")
    fun buscar(filtro: String): Flow<List<AutoparteEntity>>

    @Insert suspend fun insert(autoparte: AutoparteEntity)
}
