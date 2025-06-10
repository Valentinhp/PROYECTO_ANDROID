package com.project.rc_mecha_maint.ui.mas.perfil

import androidx.lifecycle.*
import com.project.rc_mecha_maint.data.entity.UserProfile
import com.project.rc_mecha_maint.data.repository.UserProfileRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para el módulo Perfil.
 */
class UserProfileViewModel(private val repo: UserProfileRepository) : ViewModel() {

    val user: LiveData<UserProfile?> = repo.getUser()

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch { repo.insertOrUpdate(profile) }
    }

    fun deleteUser() {
        viewModelScope.launch { repo.deleteUser() }
    }
}

/**
 * Factory para inyectar el repositorio en el ViewModel.
 */
class UserProfileViewModelFactory(
    private val repo: UserProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserProfileViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
