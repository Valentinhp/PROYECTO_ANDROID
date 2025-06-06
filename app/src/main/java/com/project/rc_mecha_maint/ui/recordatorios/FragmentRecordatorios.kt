// ui/recordatorios/FragmentRecordatorios.kt

package com.project.rc_mecha_maint.ui.recordatorios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.rc_mecha_maint.data.entity.Reminder
import com.project.rc_mecha_maint.databinding.FragmentRecordatoriosBinding

/**
 * Fragment que muestra la lista de recordatorios y permite
 * editar o eliminar cada uno. Usa View Binding para encontrar
 * las vistas en lugar de Kotlin synthetics.
 */
class FragmentRecordatorios : Fragment() {

    // ViewModel para acceder a los datos
    private val viewModel: ReminderViewModel by viewModels()

    // Adaptador para el RecyclerView
    private lateinit var adapter: ReminderAdapter

    // Variable para el View Binding; _binding puede ser null fuera de onCreate/onDestroy
    private var _binding: FragmentRecordatoriosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout usando View Binding
        _binding = FragmentRecordatoriosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Inicializar el adaptador, definiendo qué hacer al editar o eliminar
        adapter = ReminderAdapter(
            onEditClick = { reminder ->
                // Navegar al formulario con el objeto Reminder como argumento
                // Se asume que usas Safe Args y que existe la dirección action_recordatorios_to_nuevoRecordatorio
                val action = FragmentRecordatoriosDirections
                    .actionRecordatoriosToNuevoRecordatorio(reminder)
                findNavController().navigate(action)
            },
            onDeleteClick = { reminder ->
                // Eliminamos el recordatorio y mostramos un mensaje
                viewModel.deleteReminder(reminder)
                Snackbar.make(binding.rvRecordatorios, "Recordatorio eliminado", Snackbar.LENGTH_SHORT).show()
            }
        )

        // 2) Configurar el RecyclerView con LayoutManager y el adaptador
        binding.rvRecordatorios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecordatorios.adapter = adapter

        // 3) Observar la lista de recordatorios desde el ViewModel
        viewModel.allReminders.observe(viewLifecycleOwner) { lista ->
            // “submitList” actualiza la lista que muestra el adaptador
            adapter.submitList(lista)
        }

        // 4) El FloatingActionButton para agregar nuevo recordatorio
        binding.fabAgregar.setOnClickListener {
            // Si no pasamos un Reminder, estamos creando uno nuevo (argumento null)
            val action = FragmentRecordatoriosDirections
                .actionRecordatoriosToNuevoRecordatorio(null)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Muy importante: liberar la referencia al binding para evitar fugas de memoria
        _binding = null
    }
}
