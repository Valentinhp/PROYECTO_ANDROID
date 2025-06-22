package com.project.rc_mecha_maint.ui.vehiculos

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.entity.Vehicle
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FragmentNuevoVehiculo : Fragment() {

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
    private lateinit var inputLayoutKilometraje: TextInputLayout
    private lateinit var inputLayoutTipoCombustible: TextInputLayout

    private lateinit var editTextMarca: TextInputEditText
    private lateinit var editTextModelo: TextInputEditText
    private lateinit var editTextAnio: TextInputEditText
    private lateinit var editTextMatricula: TextInputEditText
    private lateinit var editTextKilometraje: TextInputEditText
    private lateinit var editTextTipoCombustible: TextInputEditText

    private lateinit var buttonSave: Button

    private var photoUri: Uri? = null

    // Vehículo a editar, si viene en bundle
    private var vehicleToEdit: Vehicle? = null

    /** 1) Lanzador para tomar foto vía cámara */
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            imgPhoto.setImageURI(photoUri)
        } else {
            Toast.makeText(requireContext(), "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    /** 2) Lanzador para pedir permiso de cámara */
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Si concedió el permiso, ahora sí lanzamos la cámara
            launchCamera()
        } else {
            Toast.makeText(requireContext(),
                "Necesito permiso de cámara para tomar fotos",
                Toast.LENGTH_SHORT).show()
        }
    }

    /** 3) Lanzador para elegir foto de galería (copiando a archivo interno) */
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val input = requireContext().contentResolver
                    .openInputStream(selectedUri)
                    ?: throw Exception("No pude abrir imagen")
                val file = createImageFile()
                FileOutputStream(file).use { out ->
                    input.copyTo(out)
                }
                val savedUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                photoUri = savedUri
                imgPhoto.setImageURI(photoUri)
            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Error al copiar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_nuevo_vehiculo, container, false).also { view ->

        // Vincular vistas
        imgPhoto         = view.findViewById(R.id.imgVehiclePhoto)
        btnTakePhoto     = view.findViewById(R.id.btnTakePhoto)
        btnChooseGallery = view.findViewById(R.id.btnChooseGallery)

        inputLayoutMarca      = view.findViewById(R.id.inputLayoutMarca)
        inputLayoutModelo     = view.findViewById(R.id.inputLayoutModelo)
        inputLayoutAnio       = view.findViewById(R.id.inputLayoutAnio)
        inputLayoutMatricula  = view.findViewById(R.id.inputLayoutMatricula)
        inputLayoutKilometraje     = view.findViewById(R.id.inputLayoutKilometraje)
        inputLayoutTipoCombustible = view.findViewById(R.id.inputLayoutTipoCombustible)

        editTextMarca         = view.findViewById(R.id.editTextMarca)
        editTextModelo        = view.findViewById(R.id.editTextModelo)
        editTextAnio          = view.findViewById(R.id.editTextAnio)
        editTextMatricula     = view.findViewById(R.id.editTextMatricula)
        editTextKilometraje   = view.findViewById(R.id.editTextKilometraje)
        editTextTipoCombustible = view.findViewById(R.id.editTextTipoCombustible)

        buttonSave = view.findViewById(R.id.buttonSave)

        // Leer argumento opcional “vehicle” si viene
        arguments?.let { bundle ->
            if (bundle.containsKey("vehicle")) {
                vehicleToEdit = try {
                    FragmentNuevoVehiculoArgs.fromBundle(bundle).vehicle
                } catch (_: Exception) {
                    null
                }
            }
        }

        // Si editamos, precargamos campos
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

        // Botón “Tomar foto”: primero pedimos permiso si es necesario
        btnTakePhoto.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tiene permiso: lanzamos cámara
                    launchCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    Toast.makeText(requireContext(),
                        "Necesitamos el permiso de cámara para tomar fotos",
                        Toast.LENGTH_SHORT).show()
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                else -> {
                    // Primera vez o nunca preguntó: lanzamos petición
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        // Botón “Elegir galería”
        btnChooseGallery.setOnClickListener {
            pickGalleryLauncher.launch("image/*")
        }

        // Guardar vehículo
        buttonSave.setOnClickListener { saveVehicle() }
    }

    /** Invocado una vez que sabemos que tenemos permiso de cámara */
    private fun launchCamera() {
        val file = createImageFile()
        // obtenemos la URI para FileProvider
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        takePictureLauncher.launch(photoUri)
    }

    private fun saveVehicle() {
        val marca     = editTextMarca.text.toString().trim()
        val modelo    = editTextModelo.text.toString().trim()
        val anio      = editTextAnio.text.toString().toIntOrNull()
        val matricula = editTextMatricula.text.toString().trim()
        val km        = editTextKilometraje.text.toString().toIntOrNull()
        val comb      = editTextTipoCombustible.text.toString().trim()

        var ok = true
        if (marca.isEmpty())      { inputLayoutMarca.error = "Escribe la marca"; ok = false }
        else inputLayoutMarca.error = null

        if (modelo.isEmpty())     { inputLayoutModelo.error = "Escribe el modelo"; ok = false }
        else inputLayoutModelo.error = null

        if (anio == null)         { inputLayoutAnio.error = "Año inválido"; ok = false }
        else inputLayoutAnio.error = null

        if (matricula.isEmpty())  { inputLayoutMatricula.error = "Escribe matrícula"; ok = false }
        else inputLayoutMatricula.error = null

        if (km == null || km < 0) { inputLayoutKilometraje.error = "Kilometraje inválido"; ok = false }
        else inputLayoutKilometraje.error = null

        if (comb.isEmpty())       { inputLayoutTipoCombustible.error = "Escribe combustible"; ok = false }
        else inputLayoutTipoCombustible.error = null

        if (!ok) return

        if (vehicleToEdit == null) {
            val newV = Vehicle(
                marca           = marca,
                modelo          = modelo,
                anio            = anio!!,
                matricula       = matricula,
                kilometraje     = km!!,
                tipoCombustible = comb,
                photoUri        = photoUri?.toString() ?: ""
            )
            vehicleViewModel.insertVehicle(newV)
            Toast.makeText(requireContext(), "Vehículo agregado", Toast.LENGTH_SHORT).show()
        } else {
            val upd = vehicleToEdit!!.copy(
                marca           = marca,
                modelo          = modelo,
                anio            = anio!!,
                matricula       = matricula,
                kilometraje     = km!!,
                tipoCombustible = comb,
                photoUri        = photoUri?.toString() ?: ""
            )
            vehicleViewModel.updateVehicle(upd)
            Toast.makeText(requireContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }

    /** Crea un archivo temporal en external-files/Pictures */
    private fun createImageFile(): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${ts}_", ".jpg", dir)
    }
}
