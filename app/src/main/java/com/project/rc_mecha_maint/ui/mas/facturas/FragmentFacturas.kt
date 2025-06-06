package com.project.rc_mecha_maint.ui.mas.facturas

// ui/facturas/FragmentFacturas.kt


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.InvoiceRepository
import com.project.rc_mecha_maint.databinding.FragmentFacturasBinding

/**
 * FragmentFacturas: muestra todas las facturas de un registro de historial.
 * Recibe el argumento "historyId".
 */
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

        // 1) Obtener historyId de los argumentos
        val historyId = arguments?.getLong(ARG_HISTORY_ID) ?: 0L

        // 2) Inicializar ViewModel + Repositorio
        val dao = AppDatabase.getInstance(requireContext()).invoiceDao()
        val repo = InvoiceRepository(dao)
        viewModel = ViewModelProvider(this, InvoiceViewModelFactory(repo))
            .get(InvoiceViewModel::class.java)

        // 3) Configurar RecyclerView y Adapter
        adapter = InvoiceAdapter(
            onEliminarFactura = { invoice ->
                // Eliminar factura (con confirmación si quieres)
                viewModel.deleteInvoice(invoice)
            },
            onReprocesarOCR = { invoice ->
                // Aquí podrías volver a procesar OCR. Por simplicidad, mostramos un mensaje.
                // O bien lanzar otra función que abra el OCR.
            }
        )

        binding.recyclerFacturas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFacturas.adapter = adapter

        // 4) Observar LiveData de facturas
        viewModel.getInvoicesByHistory(historyId).observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        // 5) FAB para agregar factura
        binding.fabAgregarFactura.setOnClickListener {
            // Navegar a FragmentSubirFactura pasando el mismo historyId
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
