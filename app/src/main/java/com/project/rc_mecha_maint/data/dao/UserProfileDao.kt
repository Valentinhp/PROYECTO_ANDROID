package com.project.rc_mecha_maint.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.rc_mecha_maint.data.entity.UserProfile

/**
 * DAO para acceder al perfil de usuario.
 */
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile_table LIMIT 1")
    fun getUser(): LiveData<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("DELETE FROM user_profile_table")
    suspend fun deleteUser()
}
