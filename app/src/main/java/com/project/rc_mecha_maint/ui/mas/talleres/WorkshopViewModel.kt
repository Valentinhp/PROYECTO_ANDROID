package com.project.rc_mecha_maint.ui.mas.talleres

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.data.repository.WorkshopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class WorkshopViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = WorkshopRepository(
        AppDatabase.getInstance(app).workshopDao()
    )

    /** LiveData observado por el fragmento. */
    val workshops = repo.workshops

    init { precargarSiEsPrimeraVez() }

    /** Precarga JSON -> DB si la tabla está vacía. */
    private fun precargarSiEsPrimeraVez() = viewModelScope.launch(Dispatchers.IO) {
        if (repo.yaHayDatos()) return@launch
        repo.precargar(leerJson())
    }

    /** Alta / Edición. */
    fun save(w: Workshop) = viewModelScope.launch { repo.save(w) }

    /** Borrado. */
    fun delete(w: Workshop) = viewModelScope.launch { repo.delete(w) }

    /** Lee assets/workshops.json y devuelve lista. */
    private fun leerJson(): List<Workshop> {
        val texto = getApplication<Application>()
            .assets.open("workshops.json")
            .bufferedReader().use { it.readText() }

        val arr = JSONArray(texto)
        val list = mutableListOf<Workshop>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list += Workshop(
                nombre    = o.getString("nombre"),
                direccion = o.getString("direccion"),
                latitud   = o.getDouble("latitud"),
                longitud  = o.getDouble("longitud"),
                telefono  = o.getString("telefono"),
                fotoUrl   = o.getString("fotoUrl")
            )
        }
        return list
    }
}
