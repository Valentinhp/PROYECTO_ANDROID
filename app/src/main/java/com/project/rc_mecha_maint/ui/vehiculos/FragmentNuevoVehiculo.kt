package com.project.rc_mecha_maint.ui.vehiculos

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.Button

/**
 * Fragmento que muestra el formulario para crear o editar un vehículo.
 * Recibe un argumento Vehicle?; si es null, estamos creando; si no, editando.
 */
class FragmentNuevoVehiculo : Fragment() {

    // Recibir argumentos (puede que venga un Vehicle o null)
    private val args: FragmentNuevoVehiculoArgs by navArgs()

    // ViewModel igual que en fragmento de lista
    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    // Referencias a vistas
    private lateinit var inputLayoutMarca: TextInputLayout
    private lateinit var inputLayoutModelo: TextInputLayout
    private lateinit var inputLayoutAnio: TextInputLayout
    private lateinit var inputLayoutMatricula: TextInputLayout

    private lateinit var editTextMarca: TextInputEditText
    private lateinit var editTextModelo: TextInputEditText
    private lateinit var editTextAnio: TextInputEditText
    private lateinit var editTextMatricula: TextInputEditText

    private lateinit var buttonSave: Button

    // Vehículo que se edita (puede ser null si venimos a crear)
    private var vehicleToEdit: Vehicle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del formulario
        val view = inflater.inflate(R.layout.fragment_nuevo_vehiculo, container, false)

        // Obtener referencias
        inputLayoutMarca = view.findViewById(R.id.inputLayoutMarca)
        inputLayoutModelo = view.findViewById(R.id.inputLayoutModelo)
        inputLayoutAnio = view.findViewById(R.id.inputLayoutAnio)
        inputLayoutMatricula = view.findViewById(R.id.inputLayoutMatricula)

        editTextMarca = view.findViewById(R.id.editTextMarca)
        editTextModelo = view.findViewById(R.id.editTextModelo)
        editTextAnio = view.findViewById(R.id.editTextAnio)
        editTextMatricula = view.findViewById(R.id.editTextMatricula)

        buttonSave = view.findViewById(R.id.buttonSave)

        // Revisar si vino un Vehicle por argumento (editar) o null (crear)
        vehicleToEdit = args.vehicle // Desde nav_graph, se genera vehicleToEdit

        if (vehicleToEdit != null) {
            // Si vamos a editar, precargamos los campos
            editTextMarca.setText(vehicleToEdit!!.marca)
            editTextModelo.setText(vehicleToEdit!!.modelo)
            editTextAnio.setText(vehicleToEdit!!.anio.toString())
            editTextMatricula.setText(vehicleToEdit!!.matricula)
        }

        // Al hacer clic en “Guardar”, validamos y luego insertamos o actualizamos
        buttonSave.setOnClickListener {
            saveVehicle()
        }

        return view
    }

    /**
     * Validar campos y luego crear un objeto Vehicle para insertarlo o actualizarlo.
     */
    private fun saveVehicle() {
        // Sacar texto de cada campo
        val marca = editTextMarca.text.toString().trim()
        val modelo = editTextModelo.text.toString().trim()
        val anioStr = editTextAnio.text.toString().trim()
        val matricula = editTextMatricula.text.toString().trim()

        // Validaciones simples: que ninguno esté vacío
        var isValid = true

        if (TextUtils.isEmpty(marca)) {
            inputLayoutMarca.error = "Escribe la marca"
            isValid = false
        } else {
            inputLayoutMarca.error = null
        }

        if (TextUtils.isEmpty(modelo)) {
            inputLayoutModelo.error = "Escribe el modelo"
            isValid = false
        } else {
            inputLayoutModelo.error = null
        }

        if (TextUtils.isEmpty(anioStr)) {
            inputLayoutAnio.error = "Escribe el año"
            isValid = false
        } else {
            // Además verificar que sea número
            val anioInt = anioStr.toIntOrNull()
            if (anioInt == null) {
                inputLayoutAnio.error = "Año inválido"
                isValid = false
            } else {
                inputLayoutAnio.error = null
            }
        }

        if (TextUtils.isEmpty(matricula)) {
            inputLayoutMatricula.error = "Escribe la matrícula"
            isValid = false
        } else {
            inputLayoutMatricula.error = null
        }

        if (!isValid) {
            // Si algo falló, no continuar
            return
        }

        // Convertir año a Int sabiendo que ya es válido
        val anio = anioStr.toInt()

        if (vehicleToEdit == null) {
            // Crear uno nuevo con id=0 (Room lo generará al insertar)
            val newVehicle = Vehicle(
                marca = marca,
                modelo = modelo,
                anio = anio,
                matricula = matricula
            )
            vehicleViewModel.insertVehicle(newVehicle)
            Toast.makeText(requireContext(), "Vehículo agregado", Toast.LENGTH_SHORT).show()
        } else {
            // Actualizar los campos del vehículo existente
            val updatedVehicle = vehicleToEdit!!.copy(
                marca = marca,
                modelo = modelo,
                anio = anio,
                matricula = matricula
            )
            vehicleViewModel.updateVehicle(updatedVehicle)
            Toast.makeText(requireContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show()
        }

        // Volver a la lista (popBackStack) para que se vea el cambio
        findNavController().popBackStack()
    }
}
