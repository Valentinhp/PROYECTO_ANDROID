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

    // Para navegación segura y recibir datos si venimos a EDITAR
    private val args: FragmentNuevoVehiculoArgs by navArgs()
    private val vehicleViewModel: VehicleViewModel by viewModels {
        VehicleViewModelFactory(requireActivity().application)
    }

    // Vistas existentes
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

    // **Nuevas vistas para Kilometraje y Tipo de Combustible**
    private lateinit var inputLayoutKilometraje: TextInputLayout
    private lateinit var inputLayoutTipoCombustible: TextInputLayout
    private lateinit var editTextKilometraje: TextInputEditText
    private lateinit var editTextTipoCombustible: TextInputEditText

    private lateinit var buttonSave: Button

    // Para guardar la URI de la foto
    private var photoUri: Uri? = null
    private var vehicleToEdit: Vehicle? = null

    // Lanzador para tomar foto con cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            imgPhoto.setImageURI(photoUri)
        } else {
            Toast.makeText(requireContext(), "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para elegir foto de galería
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            imgPhoto.setImageURI(photoUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos el layout
        val view = inflater.inflate(R.layout.fragment_nuevo_vehiculo, container, false)

        // 1) Vinculamos vistas de imagen y botones
        imgPhoto         = view.findViewById(R.id.imgVehiclePhoto)
        btnTakePhoto     = view.findViewById(R.id.btnTakePhoto)
        btnChooseGallery = view.findViewById(R.id.btnChooseGallery)

        // 2) Vinculamos layouts y EditText de los campos existentes
        inputLayoutMarca      = view.findViewById(R.id.inputLayoutMarca)
        inputLayoutModelo     = view.findViewById(R.id.inputLayoutModelo)
        inputLayoutAnio       = view.findViewById(R.id.inputLayoutAnio)
        inputLayoutMatricula  = view.findViewById(R.id.inputLayoutMatricula)

        editTextMarca         = view.findViewById(R.id.editTextMarca)
        editTextModelo        = view.findViewById(R.id.editTextModelo)
        editTextAnio          = view.findViewById(R.id.editTextAnio)
        editTextMatricula     = view.findViewById(R.id.editTextMatricula)

        // 3) Vinculamos los nuevos layouts y EditText
        inputLayoutKilometraje       = view.findViewById(R.id.inputLayoutKilometraje)
        inputLayoutTipoCombustible   = view.findViewById(R.id.inputLayoutTipoCombustible)

        editTextKilometraje          = view.findViewById(R.id.editTextKilometraje)
        editTextTipoCombustible      = view.findViewById(R.id.editTextTipoCombustible)

        // 4) Botón guardar
        buttonSave = view.findViewById(R.id.buttonSave)

        // Verificamos si venimos a EDITAR un vehículo existente
        vehicleToEdit = try {
            args.vehicle
        } catch (_: Exception) {
            null
        }

        // Si es edición, llenamos los campos con datos existentes
        vehicleToEdit?.let { v ->
            editTextMarca.setText(v.marca)
            editTextModelo.setText(v.modelo)
            editTextAnio.setText(v.anio.toString())
            editTextMatricula.setText(v.matricula)
            editTextKilometraje.setText(v.kilometraje.toString())
            editTextTipoCombustible.setText(v.tipoCombustible)
            v.photoUri?.let {
                photoUri = Uri.parse(it)
                imgPhoto.setImageURI(photoUri)
            }
        }

        // Configuramos los click listeners de foto/galería
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

        // Al dar guardar, llamamos a saveVehicle()
        buttonSave.setOnClickListener { saveVehicle() }

        return view
    }

    /**
     * Lee todos los campos, valida, crea o actualiza Vehicle y cierra el fragment
     */
    private fun saveVehicle() {
        // 1) Leemos texto y quitamos espacios sobrantes
        val marca     = editTextMarca.text.toString().trim()
        val modelo    = editTextModelo.text.toString().trim()
        val anioStr   = editTextAnio.text.toString().trim()
        val matricula = editTextMatricula.text.toString().trim()
        val kmStr     = editTextKilometraje.text.toString().trim()
        val comb      = editTextTipoCombustible.text.toString().trim()

        var valid = true

        // 2) Validaciones de cada campo
        if (TextUtils.isEmpty(marca)) {
            inputLayoutMarca.error = "Escribe la marca"
            valid = false
        } else inputLayoutMarca.error = null

        if (TextUtils.isEmpty(modelo)) {
            inputLayoutModelo.error = "Escribe el modelo"
            valid = false
        } else inputLayoutModelo.error = null

        val anio = anioStr.toIntOrNull()
        if (anioStr.isEmpty() || anio == null) {
            inputLayoutAnio.error = "Año inválido"
            valid = false
        } else inputLayoutAnio.error = null

        if (TextUtils.isEmpty(matricula)) {
            inputLayoutMatricula.error = "Escribe la matrícula"
            valid = false
        } else inputLayoutMatricula.error = null

        // Validamos kilometraje (entero >= 0)
        val km = kmStr.toIntOrNull()
        if (kmStr.isEmpty() || km == null || km < 0) {
            inputLayoutKilometraje.error = "Kilometraje inválido"
            valid = false
        } else inputLayoutKilometraje.error = null

        // Validamos tipo de combustible (no vacío)
        if (TextUtils.isEmpty(comb)) {
            inputLayoutTipoCombustible.error = "Escribe el combustible"
            valid = false
        } else inputLayoutTipoCombustible.error = null

        if (!valid) return  // si algo falla, salimos

        // 3) Creamos o actualizamos el objeto Vehicle
        if (vehicleToEdit == null) {
            // Nuevo vehículo
            val newV = Vehicle(
                marca           = marca,
                modelo          = modelo,
                anio            = anio!!,
                matricula       = matricula,
                kilometraje     = km!!,
                tipoCombustible = comb,
                photoUri        = photoUri?.toString()
            )
            vehicleViewModel.insertVehicle(newV)
            Toast.makeText(requireContext(), "Vehículo agregado", Toast.LENGTH_SHORT).show()
        } else {
            // Actualización de existente
            val upd = vehicleToEdit!!.copy(
                marca           = marca,
                modelo          = modelo,
                anio            = anio!!,
                matricula       = matricula,
                kilometraje     = km!!,
                tipoCombustible = comb,
                photoUri        = photoUri?.toString()
            )
            vehicleViewModel.updateVehicle(upd)
            Toast.makeText(requireContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show()
        }

        // 4) Volvemos atrás
        findNavController().popBackStack()
    }

    /**
     * Crea un archivo temporal para guardar la foto
     */
    private fun createImageFile(): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${ts}_", ".jpg", dir)
    }
}
