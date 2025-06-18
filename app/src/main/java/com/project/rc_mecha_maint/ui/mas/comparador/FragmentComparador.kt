// app/src/main/java/com/project/rc_mecha_maint/ui/mas/comparador/FragmentComparador.kt
package com.project.rc_mecha_maint.ui.mas.comparador

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.data.entity.AutoparteJson
import com.project.rc_mecha_maint.databinding.FragmentComparadorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentComparador : Fragment() {
    private var _b: FragmentComparadorBinding? = null
    private val b get() = _b!!

    private lateinit var allAutopartes: List<AutoparteEntity>
    private lateinit var adapter: ComparadorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentComparadorBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Preparar RecyclerView y Adapter
        adapter = ComparadorAdapter { cotizacion ->
            startActivity(
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:${cotizacion.proveedor}"))
            )
        }
        b.rvComparador.layoutManager = LinearLayoutManager(requireContext())
        b.rvComparador.adapter = adapter

        // 2) Leer JSON + talleres, mapear a AutoparteEntity y llenar Spinner
        lifecycleScope.launch(Dispatchers.IO) {
            // 2.1) JSON de cotizaciones
            val jsonText = requireContext().assets
                .open("autopartes.json")
                .bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<AutoparteJson>>() {}.type
            val listaJson: List<AutoparteJson> = Gson().fromJson(jsonText, listType)

            // 2.2) Talleres de Room
            val talleres = AppDatabase
                .getInstance(requireContext())
                .workshopDao()
                .getAllSync()

            // 2.3) Mapeo a AutoparteEntity
            allAutopartes = listaJson.mapIndexed { index: Int, j: AutoparteJson ->
                val tall = talleres.find { it.id == j.workshopId }
                AutoparteEntity(
                    id = index.toLong(),
                    clave = j.clave,
                    descripcion = j.descripcion,
                    proveedor = tall?.nombre ?: "Taller #${j.workshopId}",
                    precio = j.precio
                )
            }

            // 2.4) Pasa al hilo UI para poblar Spinner y listener
            withContext(Dispatchers.Main) {
                val descripciones = allAutopartes
                    .map { it.descripcion }
                    .distinct()

                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    descripciones
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                b.spinnerAutopartes.adapter = spinnerAdapter

                b.spinnerAutopartes.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val desc = descripciones[position]
                            val filtrado = allAutopartes.filter { it.descripcion == desc }
                            adapter.submitList(filtrado)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {
                            adapter.submitList(emptyList())
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
