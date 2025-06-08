package com.project.rc_mecha_maint.ui.mas.comparador

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        adapter = ComparadorAdapter { w ->
            val a = FragmentComparadorDirections.actionComparadorToDetalleTaller(w.id)
            findNavController().navigate(a)
        }
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter
        b.btnBuscar.setOnClickListener { filtrar() }
    }

    private fun filtrar() {
        val servicio = b.spinner.selectedItem.toString()
        vm.workshops.value?.let { lista ->
            val precios = lista.map { PriceItem(it, Random.nextInt(500, 1500)) }
                .sortedBy { it.precio }
            adapter.submitList(precios)
            b.txtHeader.text = "Precios para '$servicio' (${precios.size})"
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
