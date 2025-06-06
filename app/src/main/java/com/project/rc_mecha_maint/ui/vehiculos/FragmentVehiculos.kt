package com.project.rc_mecha_maint.ui.vehiculos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragmento que muestra la lista de vehículos y permite navegar al formulario.
 */
class FragmentVehiculos : Fragment() {

    // Obtener el ViewModel usando la Factory para pasar el Application
    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    private lateinit var adapter: VehicleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout fragment_vehiculos.xml
        val view = inflater.inflate(R.layout.fragment_vehiculos, container, false)

        // Referencias a vistas
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewVehicles)
        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAddVehicle)

        // Configurar RecyclerView con LinearLayoutManager y adapter vacío
        adapter = VehicleAdapter(
            onEditClick = { vehicle -> navigateToEdit(vehicle) },
            onDeleteClick = { vehicle -> deleteVehicle(vehicle) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observar la lista de vehículos del ViewModel
        vehicleViewModel.allVehicles.observe(viewLifecycleOwner, Observer { vehicles ->
            // Cuando cambie la lista, submit al adapter
            adapter.submitList(vehicles)
        })

        // Al tocar el FAB, navegar al formulario para crear un vehículo nuevo
        fabAdd.setOnClickListener {
            val action = FragmentVehiculosDirections
                .actionVehiculosToNuevoVehiculo(null) // null indica que no venimos a editar
            findNavController().navigate(action)
        }

        return view
    }

    /**
     * Navegar al formulario para editar. Le pasamos el objeto Vehicle como argumento.
     */
    private fun navigateToEdit(vehicle: Vehicle) {
        val action = FragmentVehiculosDirections
            .actionVehiculosToNuevoVehiculo(vehicle)
        findNavController().navigate(action)
    }

    /**
     * Llamar al ViewModel para borrar un vehículo.
     * Cuando termine, la lista se actualiza automáticamente.
     */
    private fun deleteVehicle(vehicle: Vehicle) {
        vehicleViewModel.deleteVehicle(vehicle)
    }
}
