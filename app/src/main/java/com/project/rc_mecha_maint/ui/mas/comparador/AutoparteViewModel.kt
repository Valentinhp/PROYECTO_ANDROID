package com.project.rc_mecha_maint.ui.mas.comparador

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.data.repository.AutoparteRepository
import kotlinx.coroutines.launch

class AutoparteViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AutoparteRepository(
        AppDatabase.getInstance(app).autoparteDao()
    )
    fun buscarPiezas(filtro: String) =
        repo.buscarPiezas(filtro).asLiveData()
    fun guardarCotizacion(pieza: AutoparteEntity) = viewModelScope.launch {
        repo.guardar(pieza)
    }
}
