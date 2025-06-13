package com.project.rc_mecha_maint.ui.mas.talleres

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Workshop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkshopViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).workshopDao()

    // 1) Todos los talleres
    val talleres: LiveData<List<Workshop>> = dao.getAll().asLiveData()

    // 2) Un taller por ID
    fun getById(id: Long): LiveData<Workshop?> =
        dao.getById(id).asLiveData()

    // 3) Inserta o actualiza un taller
    fun saveWorkshop(workshop: Workshop) {
        viewModelScope.launch {
            dao.insert(workshop)
        }
    }

    // 4) Carga inicial desde assets (JSON)
    fun loadFromAssets() {
        viewModelScope.launch {
            val json = withContext(Dispatchers.IO) {
                getApplication<Application>().assets
                    .open("workshops.json")
                    .bufferedReader().use { it.readText() }
            }
            val list: List<Workshop> = Gson().fromJson(
                json,
                object : TypeToken<List<Workshop>>() {}.type
            )
            dao.insertAll(list)
        }
    }
}
