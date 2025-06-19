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
import androidx.navigation.fragment.navArgs
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

    // Recogemos el argumento
    private val args: FragmentComparadorArgs by navArgs()

    private lateinit var allAutopartes: List<AutoparteEntity>
    private lateinit var adapter: ComparadorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentComparadorBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) RecyclerView + Adapter
        adapter = ComparadorAdapter { phone ->
            if (phone.isNotBlank()) startActivity(
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            )
        }
        b.rvComparador.layoutManager = LinearLayoutManager(requireContext())
        b.rvComparador.adapter = adapter

        // 2) Cargo JSON + Talleres
        lifecycleScope.launch(Dispatchers.IO) {
            val jsonText = requireContext().assets
                .open("autopartes.json")
                .bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<AutoparteJson>>() {}.type
            val listaJson: List<AutoparteJson> = Gson().fromJson(jsonText, listType)

            val talleres = AppDatabase
                .getInstance(requireContext())
                .workshopDao()
                .getAllSync()

            allAutopartes = listaJson.mapIndexed { idx, j ->
                val tall = talleres.find { it.id == j.workshopId }
                AutoparteEntity(
                    id          = idx.toLong(),
                    clave       = j.clave,
                    descripcion = j.descripcion,
                    proveedor   = tall?.nombre ?: "Taller #${j.workshopId}",
                    telefono    = tall?.telefono ?: "",
                    precio      = j.precio.toFloat()
                )
            }

            withContext(Dispatchers.Main) {
                val descripciones = allAutopartes.map { it.descripcion }.distinct()

                // Pre-selección si venimos de Diagnóstico
                val selected = args.failureName
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    descripciones
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                b.spinnerAutopartes.adapter = spinnerAdapter
                if (selected.isNotEmpty()) {
                    val pos = descripciones.indexOf(selected)
                    if (pos >= 0) b.spinnerAutopartes.setSelection(pos)
                }

                b.spinnerAutopartes.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>, view: View?, position: Int, id: Long
                        ) {
                            val sel = descripciones[position]
                            adapter.submitList(
                                allAutopartes.filter { it.descripcion == sel }
                            )
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
