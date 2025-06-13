package com.project.rc_mecha_maint.ui.mas.comparador

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.databinding.FragmentAutopartesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAutopartes : Fragment() {
    private var _binding: FragmentAutopartesBinding? = null
    private val b get() = _binding!!

    private lateinit var allPiezas: List<AutoparteEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAutopartesBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 1) Configura RecyclerView
        val adapter = AutoparteAdapter { pieza ->
            Toast.makeText(requireContext(),
                "Guardada: ${pieza.clave}",
                Toast.LENGTH_SHORT
            ).show()
            // aquí podrías llamar a vm.guardarCotizacion(pieza)
        }
        b.rvAutopartes.layoutManager = LinearLayoutManager(requireContext())
        b.rvAutopartes.adapter = adapter

        // 2) Carga JSON en background
        b.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            allPiezas = loadJsonFromAssets(requireContext())
            adapter.submitList(allPiezas)
            b.progressBar.visibility = View.GONE
        }

        // 3) Búsqueda y empty state
        b.btnBuscarPiezas.setOnClickListener {
            hideKeyboard()
            val filtro = b.etBuscarPieza.text.toString().trim().lowercase()
            val filtrado = if (filtro.isEmpty()) allPiezas
            else allPiezas.filter { pieza ->
                pieza.clave.lowercase().contains(filtro) ||
                        pieza.descripcion.lowercase().contains(filtro)
            }
            adapter.submitList(filtrado)
            b.tvNoResults.visibility =
                if (filtrado.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Lee y parsea el JSON desde assets en un hilo IO */
    private suspend fun loadJsonFromAssets(ctx: Context): List<AutoparteEntity> =
        withContext(Dispatchers.IO) {
            val text = ctx.assets.open("autopartes.json")
                .bufferedReader()
                .use { it.readText() }
            Gson().fromJson(
                text,
                object : TypeToken<List<AutoparteEntity>>() {}.type
            )
        }

    /** Oculta el teclado tras pulsar Buscar */
    private fun hideKeyboard() {
        val imm = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(b.etBuscarPieza.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
