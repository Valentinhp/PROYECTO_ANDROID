package com.project.rc_mecha_maint.ui.vehiculos

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle

class FragmentVehiculos : Fragment(R.layout.fragment_vehiculos) {

    private val vm: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    private lateinit var adapter: VehicleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Referencias a vistas
        val rv    = view.findViewById<RecyclerView>(R.id.recyclerViewVehicles)
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshVehicles)
        val fab   = view.findViewById<FloatingActionButton>(R.id.fabAddVehicle)

        // 2) Configura RecyclerView y Adapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = VehicleAdapter(
            // Al tocar editar
            onEditClick = { vehicle ->
                val action = FragmentVehiculosDirections
                    .actionVehiculosToNuevoVehiculo(vehicle)
                findNavController().navigate(action)
            },
            // Al tocar eliminar
            onDeleteClick = { vehicle ->
                vm.deleteVehicle(vehicle)
                Toast.makeText(requireContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show()
            }
        )
        rv.adapter = adapter

        // 3) Swipe-to-delete (swipe izquierda)
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val veh = adapter.currentList[pos]
                vm.deleteVehicle(veh)
                Toast.makeText(requireContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rv)

        // 4) Pull-to-refresh (solo quita spinner, la BD ya actualiza automáticamente)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

        // 5) FAB para agregar
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_vehiculos_to_nuevoVehiculo)
        }

        // 6) Observa los vehículos y actualiza la lista
        swipe.isRefreshing = true
        vm.allVehicles.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
            swipe.isRefreshing = false
        })
    }
}
