package com.project.rc_mecha_maint.ui.mas.historial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.HistoryRepository
import com.project.rc_mecha_maint.databinding.FragmentHistorialBinding

/**
 * FragmentHistorial: muestra la lista de históricos para un vehicleId dado.
 * Recibe el argumento "vehicleId" desde quien llame a este fragment.
 */
class FragmentHistorial : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

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

        // 1) Obtener el vehicleId de los argumentos
        val vehicleId = arguments?.getLong(ARG_VEHICLE_ID) ?: 0L

        // 2) Inicializar ViewModel + Repositorio
        val dao = AppDatabase.getInstance(requireContext()).historyDao()
        val repo = HistoryRepository(dao)
        viewModel = ViewModelProvider(this, HistoryViewModelFactory(repo))
            .get(HistoryViewModel::class.java)

        // 3) Configurar RecyclerView y Adapter
        adapter = HistoryAdapter(
            onAgregarFactura = { history ->
                // Navegar a FragmentSubirFactura, pasando historyId en Bundle
                findNavController().navigate(
                    R.id.nav_subirFactura,
                    bundleOf("historyId" to history.id)
                )
            },
            onVerFacturas = { history ->
                // Navegar a FragmentFacturas, pasando historyId en Bundle
                findNavController().navigate(
                    R.id.nav_facturas,
                    bundleOf("historyId" to history.id)
                )
            }
        )

        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        // 4) Observar LiveData de historial y actualizar lista
        viewModel.getHistoryByVehicle(vehicleId).observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
