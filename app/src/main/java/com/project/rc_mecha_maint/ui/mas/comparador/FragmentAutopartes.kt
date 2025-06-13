package com.project.rc_mecha_maint.ui.mas.comparador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.databinding.FragmentAutopartesBinding

class FragmentAutopartes : Fragment() {
    private var _b: FragmentAutopartesBinding? = null
    private val b get() = _b!!

    private lateinit var allPiezas: List<AutoparteEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentAutopartesBinding.inflate(inflater, container, false)
        .also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 1) Leer JSON de assets y parsear
        val json = requireContext().assets
            .open("autopartes.json")
            .bufferedReader()
            .use { it.readText() }
        allPiezas = Gson().fromJson(
            json,
            object : TypeToken<List<AutoparteEntity>>() {}.type
        )

        // 2) Configurar RecyclerView
        val adapter = AutoparteAdapter { pieza ->
            // Aquí podrías guardar en BD: vm.guardarCotizacion(pieza)
        }
        b.rvAutopartes.layoutManager = LinearLayoutManager(requireContext())
        b.rvAutopartes.adapter = adapter

        // 3) Mostrar toda la lista al inicio
        adapter.submitList(allPiezas)

        // 4) Filtrar al pulsar Buscar
        b.btnBuscarPiezas.setOnClickListener {
            val filtro = b.etBuscarPieza.text.toString().trim().lowercase()
            val filtrado = if (filtro.isEmpty()) allPiezas
            else allPiezas.filter { pieza ->
                pieza.clave.lowercase().contains(filtro)
                        || pieza.descripcion.lowercase().contains(filtro)
            }
            adapter.submitList(filtrado)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
