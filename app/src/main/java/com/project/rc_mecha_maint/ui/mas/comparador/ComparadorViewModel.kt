// app/src/main/java/com/project/rc_mecha_maint/ui/mas/comparador/ComparadorViewModel.kt
package com.project.rc_mecha_maint.ui.mas.comparador

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.AutoparteRepository

class ComparadorViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AutoparteRepository(
        AppDatabase.getInstance(app).autoparteDao()
    )

    /** Devuelve las cotizaciones para la pieza seleccionada */
    fun cotizacionesPara(clave: String) =
        repo.getByClave(clave).asLiveData()
}
