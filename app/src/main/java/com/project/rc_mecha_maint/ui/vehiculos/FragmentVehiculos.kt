package com.project.rc_mecha_maint.ui.vehiculos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.rc_mecha_maint.R

class FragmentVehiculos : Fragment(R.layout.fragment_vehiculos) {

    private val vm: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: VehicleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Referencias
        val rv    = view.findViewById<RecyclerView>(R.id.recyclerViewVehicles)
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshVehicles)
        val fab   = view.findViewById<FloatingActionButton>(R.id.fabAddVehicle)

        // 2) Configura RecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = VehicleAdapter(
            onEditClick = { vehicle ->
                val action = FragmentVehiculosDirections
                    .actionVehiculosToNuevoVehiculo(vehicle)
                findNavController().navigate(action)
            },
            onDeleteClick = { vehicle ->
                vm.deleteVehicle(vehicle)
            }
        )
        rv.adapter = adapter

        // 3) Maneja el swipe: sólo ocultar spinner (la BD ya emite automáticamente)
        swipe.setOnRefreshListener {
            // Si tuvieras una función de recarga en el VM la llamarías aquí,
            // por ejemplo: vm.reloadVehicles()
            swipe.isRefreshing = false
        }

        // 4) FAB para agregar
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_vehiculos_to_nuevoVehiculo)
        }

        // 5) Observa el LiveData
        swipe.isRefreshing = true
        vm.allVehicles.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
            swipe.isRefreshing = false
        })
    }
}
