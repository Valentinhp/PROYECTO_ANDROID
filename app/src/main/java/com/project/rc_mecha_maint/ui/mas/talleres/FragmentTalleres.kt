package com.project.rc_mecha_maint.ui.mas.talleres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.databinding.FragmentTalleresBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentTalleres : Fragment() {

    private var _b: FragmentTalleresBinding? = null
    private val b get() = _b!!

    // Recibimos el argumento
    private val args: FragmentTalleresArgs by navArgs()

    private val adapter = WorkshopAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentTalleresBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.rvTalleres.layoutManager = LinearLayoutManager(requireContext())
        b.rvTalleres.adapter = adapter

        b.fabAgregarTaller.setOnClickListener {
            AddEditTallerDialog.newInstance(null)
                .show(parentFragmentManager, "AddEditTaller")
        }

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext())
                .workshopDao()
                .getAll()
                .collectLatest { list ->
                    // Si vino failureId ≠ 0, podrías filtrar aquí
                    adapter.submitList(list)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
