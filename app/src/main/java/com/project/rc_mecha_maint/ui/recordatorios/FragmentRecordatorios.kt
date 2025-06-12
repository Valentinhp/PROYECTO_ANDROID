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
import com.project.rc_mecha_maint.databinding.FragmentRecordatoriosBinding

class FragmentRecordatorios : Fragment() {

    private var _binding: FragmentRecordatoriosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordatoriosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReminderAdapter(
            onEditClick = { reminder ->
                // Navega a editar con el Reminder existente
                val action = FragmentRecordatoriosDirections
                    .actionRecordatoriosToNuevoRecordatorio(reminder)
                findNavController().navigate(action)
            },
            onDeleteClick = { reminder ->
                viewModel.deleteReminder(reminder)
                Snackbar.make(binding.root, "Recordatorio eliminado", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvRecordatorios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecordatorios.adapter = adapter

        viewModel.allReminders.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.fabAgregar.setOnClickListener {
            // Navega a crear uno nuevo (envía null)
            val action = FragmentRecordatoriosDirections
                .actionRecordatoriosToNuevoRecordatorio(null)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
