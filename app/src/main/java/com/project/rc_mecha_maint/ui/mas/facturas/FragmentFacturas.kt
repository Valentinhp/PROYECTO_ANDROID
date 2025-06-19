package com.project.rc_mecha_maint.ui.mas.facturas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.FragmentFacturasBinding
import kotlinx.coroutines.launch

class FragmentFacturas : Fragment() {

    private var _binding: FragmentFacturasBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InvoiceViewModel
    private lateinit var adapter: InvoiceAdapter

    companion object {
        const val ARG_HISTORY_ID = "historyId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Obtener historyId
        val historyId = arguments?.getLong(ARG_HISTORY_ID) ?: 0L

        // 2) Inicializar ViewModel con su Factory (pasa Application, no repositorio)
        val factory = InvoiceViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)
            .get(InvoiceViewModel::class.java)

        // 3) Configurar RecyclerView
        binding.recyclerFacturas.layoutManager = LinearLayoutManager(requireContext())

        // 4) Cargar talleres y configurar adapter dentro de una coroutine
        lifecycleScope.launch {
            val talleres: List<Workshop> = AppDatabase
                .getInstance(requireContext())
                .workshopDao()
                .getAllSync()

            adapter = InvoiceAdapter(
                talleres,
                onEliminarFactura = { invoice ->
                    viewModel.deleteInvoice(invoice)
                },
                onReprocesarOCR = { invoice ->
                    // tu lógica de reprocesado
                }
            )

            binding.recyclerFacturas.adapter = adapter

            // 5) Observar las facturas y pasárselas al adapter
            viewModel.getInvoicesByHistory(historyId).observe(viewLifecycleOwner) { lista ->
                adapter.submitList(lista)
            }
        }

        // 6) FAB para agregar factura
        binding.fabAgregarFactura.setOnClickListener {
            val action = FragmentFacturasDirections
                .actionFacturasToSubirFactura(historyId)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
