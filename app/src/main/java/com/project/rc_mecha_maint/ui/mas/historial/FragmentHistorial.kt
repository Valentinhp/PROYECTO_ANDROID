package com.project.rc_mecha_maint.ui.mas.historial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.HistoryRepository
import com.project.rc_mecha_maint.databinding.FragmentHistorialBinding

class FragmentHistorial : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: HistoryRepository
    private lateinit var adapter: HistoryAdapter
    private var vehicleId: Long = 0L

    companion object {
        const val ARG_VEHICLE_ID = "vehicleId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Leer el vehicleId que te pasaron
        vehicleId = arguments?.getLong(ARG_VEHICLE_ID) ?: 0L

        // 2) Configurar RecyclerView con LayoutManager y adapter
        adapter = HistoryAdapter(
            onAgregarFactura = { history ->
                // Navegar a subir factura
                findNavController().navigate(
                    R.id.nav_subirFactura,
                    bundleOf("historyId" to history.id)
                )
            },
            onVerFacturas = { history ->
                // Navegar a lista de facturas
                findNavController().navigate(
                    R.id.nav_facturas,
                    bundleOf("historyId" to history.id)
                )
            }
        )
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        // 3) FAB para registrar mantenimiento nuevo
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(
                R.id.action_nav_historial_to_nav_registrarMantenimiento,
                bundleOf(ARG_VEHICLE_ID to vehicleId)
            )
        }

        // 4) Inicializar repositorio y observar datos
        val dao = AppDatabase.getInstance(requireContext()).historyDao()
        repository = HistoryRepository(dao)
        repository.getHistoryByVehicle(vehicleId).observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            // Mostrar mensaje vacío si no hay datos
            if (lista.isEmpty()) {
                binding.recyclerHistory.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerHistory.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
