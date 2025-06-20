package com.project.rc_mecha_maint.ui.mas.diagnostico

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.repository.DiagnosticoRepository
import com.project.rc_mecha_maint.databinding.FragmentDiagnosticoBinding
import com.project.rc_mecha_maint.util.toast

class FragmentDiagnostico : Fragment() {
    private var _b: FragmentDiagnosticoBinding? = null
    private val b get() = _b!!
    private lateinit var vm: DiagnosticoViewModel
    private val selectedIds = mutableListOf<Long>()
    private lateinit var adapter: SintomaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _b = FragmentDiagnosticoBinding.inflate(inflater, container, false)

        // Init ViewModel
        val db = AppDatabase.getInstance(requireContext())
        val repo = DiagnosticoRepository(db.symptomDao(), db.failureDao())
        val factory = DiagnosticoViewModelFactory(repo)
        vm = ViewModelProvider(this, factory)[DiagnosticoViewModel::class.java]

        // Adapter como propiedad
        adapter = SintomaAdapter { id, checked ->
            if (checked) selectedIds.add(id) else selectedIds.remove(id)
        }

        with(b) {
            rvSintomas.layoutManager = LinearLayoutManager(context)
            rvSintomas.adapter = adapter

            // Carga síntomas una sola vez
            vm.allSymptoms.observe(viewLifecycleOwner) { adapter.submitList(it) }

            btnDiagnosticar.setOnClickListener {
                // Usar observeOnce para no acumular observers
                vm.diagnose(selectedIds).observe(viewLifecycleOwner) { falla ->
                    if (falla == null) {
                        requireContext().toast("No se encontró coincidencia")
                    } else {
                        val action = FragmentDiagnosticoDirections
                            .actionFragmentDiagnosticoToFragmentResultadoDiag(falla)
                        findNavController().navigate(action)
                    }
                    // Siempre limpiar selección y UI después
                    selectedIds.clear()
                    adapter.clearAllChecks()
                }
            }
        }

        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
