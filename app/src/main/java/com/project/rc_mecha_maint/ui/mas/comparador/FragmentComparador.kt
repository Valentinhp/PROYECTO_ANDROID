package com.project.rc_mecha_maint.ui.mas.comparador

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.FragmentComparadorBinding
import com.project.rc_mecha_maint.ui.mas.talleres.WorkshopViewModel
import kotlin.random.Random

class FragmentComparador : Fragment() {

    private var _b: FragmentComparadorBinding? = null
    private val b get() = _b!!
    private val vm by activityViewModels<WorkshopViewModel>()
    private lateinit var adapter: ComparadorAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentComparadorBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        // 1) Llenar el Spinner con el array de strings
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.servicios,
            android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            b.spinner.adapter = it
        }

        // 2) Configurar Recycler
        adapter = ComparadorAdapter { w ->
            val act = FragmentComparadorDirections.actionComparadorToDetalleTaller(w.id)
            findNavController().navigate(act)
        }
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        // 3) Observar la lista de talleres
        vm.workshops.observe(viewLifecycleOwner) { adapter.submitList(emptyList()) /* vacío hasta filtrar */ }

        // 4) Botón Buscar
        b.btnBuscar.setOnClickListener { filtrar() }
    }

    /** Genera lista de precios y la muestra */
    private fun filtrar() {
        val item = b.spinner.selectedItem
        if (item == null) {
            Toast.makeText(requireContext(), "Selecciona un servicio", Toast.LENGTH_SHORT).show()
            return
        }

        val servicio = item.toString()

        val talleres: List<Workshop> = vm.workshops.value ?: emptyList()
        if (talleres.isEmpty()) {
            Toast.makeText(requireContext(), "No hay talleres cargados", Toast.LENGTH_SHORT).show()
            return
        }

        val precios = talleres.map { PriceItem(it, Random.nextInt(500, 1500)) }
            .sortedBy { it.precio }

        adapter.submitList(precios)
        b.txtHeader.text = "Precios para '$servicio' (${precios.size})"
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
