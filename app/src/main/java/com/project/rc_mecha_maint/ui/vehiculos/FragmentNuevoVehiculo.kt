package com.project.rc_mecha_maint.ui.vehiculos

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FragmentNuevoVehiculo : Fragment() {

    private val args: FragmentNuevoVehiculoArgs by navArgs()
    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    private lateinit var imgPhoto: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnChooseGallery: Button
    private lateinit var inputLayoutMarca: TextInputLayout
    private lateinit var inputLayoutModelo: TextInputLayout
    private lateinit var inputLayoutAnio: TextInputLayout
    private lateinit var inputLayoutMatricula: TextInputLayout
    private lateinit var editTextMarca: TextInputEditText
    private lateinit var editTextModelo: TextInputEditText
    private lateinit var editTextAnio: TextInputEditText
    private lateinit var editTextMatricula: TextInputEditText
    private lateinit var buttonSave: Button

    private var photoUri: Uri? = null
    private var vehicleToEdit: Vehicle? = null

    // Cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            imgPhoto.setImageURI(photoUri)
        } else {
            Toast.makeText(requireContext(), "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    // Galería
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            imgPhoto.setImageURI(photoUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_nuevo_vehiculo, container, false)

        // Vincular vistas
        imgPhoto           = view.findViewById(R.id.imgVehiclePhoto)
        btnTakePhoto       = view.findViewById(R.id.btnTakePhoto)
        btnChooseGallery   = view.findViewById(R.id.btnChooseGallery)
        inputLayoutMarca   = view.findViewById(R.id.inputLayoutMarca)
        inputLayoutModelo  = view.findViewById(R.id.inputLayoutModelo)
        inputLayoutAnio    = view.findViewById(R.id.inputLayoutAnio)
        inputLayoutMatricula = view.findViewById(R.id.inputLayoutMatricula)
        editTextMarca      = view.findViewById(R.id.editTextMarca)
        editTextModelo     = view.findViewById(R.id.editTextModelo)
        editTextAnio       = view.findViewById(R.id.editTextAnio)
        editTextMatricula  = view.findViewById(R.id.editTextMatricula)
        buttonSave         = view.findViewById(R.id.buttonSave)

        // Intentar obtener vehicle, si existe es edición
        try {
            vehicleToEdit = args.vehicle
        } catch (_: Exception) {
            vehicleToEdit = null
        }

        vehicleToEdit?.let { v ->
            editTextMarca.setText(v.marca)
            editTextModelo.setText(v.modelo)
            editTextAnio.setText(v.anio.toString())
            editTextMatricula.setText(v.matricula)
            v.photoUri?.let {
                photoUri = Uri.parse(it)
                imgPhoto.setImageURI(photoUri)
            }
        }

        btnTakePhoto.setOnClickListener {
            val file = createImageFile()
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            takePictureLauncher.launch(photoUri)
        }

        btnChooseGallery.setOnClickListener {
            pickGalleryLauncher.launch("image/*")
        }

        buttonSave.setOnClickListener { saveVehicle() }

        return view
    }

    private fun saveVehicle() {
        val marca     = editTextMarca.text.toString().trim()
        val modelo    = editTextModelo.text.toString().trim()
        val anioStr   = editTextAnio.text.toString().trim()
        val matricula = editTextMatricula.text.toString().trim()

        var valid = true
        if (TextUtils.isEmpty(marca)) {
            inputLayoutMarca.error = "Escribe la marca"; valid = false
        } else inputLayoutMarca.error = null
        if (TextUtils.isEmpty(modelo)) {
            inputLayoutModelo.error = "Escribe el modelo"; valid = false
        } else inputLayoutModelo.error = null
        val anio = anioStr.toIntOrNull()
        if (anioStr.isEmpty() || anio == null) {
            inputLayoutAnio.error = "Año inválido"; valid = false
        } else inputLayoutAnio.error = null
        if (TextUtils.isEmpty(matricula)) {
            inputLayoutMatricula.error = "Escribe la matrícula"; valid = false
        } else inputLayoutMatricula.error = null

        if (!valid) return

        if (vehicleToEdit == null) {
            val newV = Vehicle(
                marca    = marca,
                modelo   = modelo,
                anio     = anio!!,
                matricula= matricula,
                photoUri = photoUri?.toString()
            )
            vehicleViewModel.insertVehicle(newV)
            Toast.makeText(requireContext(), "Vehículo agregado", Toast.LENGTH_SHORT).show()
        } else {
            val upd = vehicleToEdit!!.copy(
                marca    = marca,
                modelo   = modelo,
                anio     = anio!!,
                matricula= matricula,
                photoUri = photoUri?.toString()
            )
            vehicleViewModel.updateVehicle(upd)
            Toast.makeText(requireContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    /** Crea un archivo temporal en /Android/data/.../Pictures */
    private fun createImageFile(): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${ts}_", ".jpg", dir)
    }
}
