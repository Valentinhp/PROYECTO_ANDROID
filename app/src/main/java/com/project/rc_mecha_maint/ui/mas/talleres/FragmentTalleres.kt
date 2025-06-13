package com.project.rc_mecha_maint.ui.mas.talleres

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.databinding.FragmentTalleresBinding

class FragmentTalleres : Fragment() {
    private var _b: FragmentTalleresBinding? = null
    private val b get() = _b!!
    private val vm by viewModels<WorkshopViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentTalleresBinding.inflate(inflater, container, false)
        .also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.loadFromAssets()

        val adapter = TallerAdapter(
            onLlamar  = { t ->
                startActivity(Intent(
                    Intent.ACTION_DIAL, Uri.parse("tel:${t.telefono}")
                ))
            },
            onCotizar = { t ->
                val args = Bundle().apply { putParcelable("workshop", t) }
                findNavController().navigate(R.id.action_global_nav_comparador, args)
            }
        )
        b.rvTalleres.layoutManager = LinearLayoutManager(requireContext())
        b.rvTalleres.adapter       = adapter

        vm.talleres.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        b.fabAgregarTaller.setOnClickListener {
            findNavController().navigate(R.id.addEditTallerDialog)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
