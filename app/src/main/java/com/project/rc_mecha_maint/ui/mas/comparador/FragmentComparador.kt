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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.AutoparteEntity
import com.project.rc_mecha_maint.data.entity.AutoparteJson
import com.project.rc_mecha_maint.data.model.Service
import com.project.rc_mecha_maint.databinding.FragmentComparadorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentComparador : Fragment() {

    private var _b: FragmentComparadorBinding? = null
    private val b get() = _b!!

    private val args: FragmentComparadorArgs by navArgs()

    private lateinit var allAutopartes: List<AutoparteEntity>
    private lateinit var allServices: List<Service>

    private lateinit var autopartesAdapter: ComparadorAdapter
    private lateinit var serviciosAdapter: ComparadorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentComparadorBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) TabLayout para Autopartes / Servicios
        b.tabLayout.apply {
            addTab(newTab().setText("Autopartes"))
            addTab(newTab().setText("Servicios"))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    b.containerAutopartes.visibility = if (tab.position == 0) View.VISIBLE else View.GONE
                    b.containerServicios.visibility = if (tab.position == 1) View.VISIBLE else View.GONE
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        // 2) Configuro RecyclerViews y Adapters
        autopartesAdapter = ComparadorAdapter { phone ->
            if (phone.isNotBlank()) startActivity(
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            )
        }
        b.rvComparador.layoutManager = LinearLayoutManager(requireContext())
        b.rvComparador.adapter = autopartesAdapter

        serviciosAdapter = ComparadorAdapter { phone ->
            if (phone.isNotBlank()) startActivity(
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            )
        }
        b.rvServicios.layoutManager = LinearLayoutManager(requireContext())
        b.rvServicios.adapter = serviciosAdapter

        // 3) Cargo datos en background
        lifecycleScope.launch(Dispatchers.IO) {
            // 3.1 Autopartes
            val autopText = requireContext().assets
                .open("autopartes.json").bufferedReader().use { it.readText() }
            val autopType = object : TypeToken<List<AutoparteJson>>() {}.type
            val autopJson: List<AutoparteJson> = Gson().fromJson(autopText, autopType)

            // 3.2 Servicios
            val servText = requireContext().assets
                .open("services.json").bufferedReader().use { it.readText() }
            val servType = object : TypeToken<List<Service>>() {}.type
            allServices = Gson().fromJson(servText, servType)

            // 3.3 Talleres
            val talleres = AppDatabase
                .getInstance(requireContext())
                .workshopDao()
                .getAllSync()

            // 3.4 Mapeo de Autopartes (aseguramos comparar Long con Long)
            allAutopartes = autopJson.mapIndexed { idx, j ->
                val t = talleres.find { it.id == j.workshopId.toLong() }
                AutoparteEntity(
                    id          = idx.toLong(),
                    clave       = j.clave,
                    descripcion = j.descripcion,
                    proveedor   = t?.nombre ?: "Taller #${j.workshopId}",
                    telefono    = t?.telefono ?: "",
                    precio      = j.precio.toFloat()
                )
            }

            withContext(Dispatchers.Main) {
                // 4) Spinner y lista de Autopartes
                val autopDesc = allAutopartes.map { it.descripcion }.distinct()
                val spA = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    autopDesc
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                b.spinnerAutopartes.adapter = spA

                args.failureName.takeIf { it.isNotEmpty() }?.let { name ->
                    autopDesc.indexOf(name).takeIf { it >= 0 }?.let(b.spinnerAutopartes::setSelection)
                }

                b.spinnerAutopartes.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                            val sel = autopDesc[pos]
                            autopartesAdapter.submitList(
                                allAutopartes.filter { it.descripcion == sel }
                            )
                        }
                        override fun onNothingSelected(p: AdapterView<*>) {
                            autopartesAdapter.submitList(emptyList())
                        }
                    }

                // 5) Spinner y lista de Servicios
                val serviciosFiltrados = args.failureId
                    .takeIf { it != -1 }
                    ?.let { fid -> allServices.filter { it.failureId == fid } }
                    ?: allServices

                val servDesc = serviciosFiltrados.map { it.descripcion }.distinct()
                val spS = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    servDesc
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                b.spinnerServicios.adapter = spS

                b.spinnerServicios.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                            val desc = servDesc[pos]
                            val selList = serviciosFiltrados.filter { it.descripcion == desc }
                            val items = selList.map { svc ->
                                val t = talleres.find { it.id == svc.workshopId.toLong() }
                                AutoparteEntity(
                                    id          = svc.id.toLong(),
                                    clave       = svc.clave,
                                    descripcion = svc.descripcion,
                                    proveedor   = t?.nombre ?: "Taller #${svc.workshopId}",
                                    telefono    = t?.telefono ?: "",
                                    precio      = svc.precio.toFloat()
                                )
                            }
                            serviciosAdapter.submitList(items)
                        }
                        override fun onNothingSelected(p: AdapterView<*>) {
                            serviciosAdapter.submitList(emptyList())
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
