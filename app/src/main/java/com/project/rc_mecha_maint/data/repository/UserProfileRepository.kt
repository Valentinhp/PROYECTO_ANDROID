package com.project.rc_mecha_maint.data.repository

import com.project.rc_mecha_maint.data.dao.UserProfileDao
import com.project.rc_mecha_maint.data.entity.UserProfile

/**
 * Repositorio para manejo de perfil de usuario.
 */
class UserProfileRepository(private val dao: UserProfileDao) {
    fun getUser() = dao.getUser()
    suspend fun insertOrUpdate(profile: UserProfile) = dao.insertOrUpdate(profile)
    suspend fun deleteUser() = dao.deleteUser()
}
