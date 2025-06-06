package com.project.rc_mecha_maint.ui.mas.facturas

// ui/facturas/FragmentSubirFactura.kt


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.repository.InvoiceRepository
import com.project.rc_mecha_maint.databinding.FragmentSubirFacturaBinding
import java.text.SimpleDateFormat
import androidx.navigation.fragment.findNavController

import java.util.*

/**
 * FragmentSubirFactura: permite tomar o seleccionar foto, procesar OCR y guardar factura.
 * Recibe "historyId" como argumento.
 */
class FragmentSubirFactura : Fragment() {

    private var _binding: FragmentSubirFacturaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InvoiceViewModel
    private var imageUri: Uri? = null

    companion object {
        const val ARG_HISTORY_ID = "historyId"
    }

    // Lanzar cámara
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            // La imagen está en imageUri (que previamente llenamos).
            binding.imagePreview.setImageURI(imageUri)
        }
    }

    // Lanzar galería
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {
            resultado.data?.data?.let { uri ->
                imageUri = uri
                binding.imagePreview.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubirFacturaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Obtener historyId
        val historyId = arguments?.getLong(ARG_HISTORY_ID) ?: 0L

        // 2) Inicializar ViewModel de Invoice
        val dao = AppDatabase.getInstance(requireContext()).invoiceDao()
        val repo = InvoiceRepository(dao)
        viewModel = ViewModelProvider(this, InvoiceViewModelFactory(repo))
            .get(InvoiceViewModel::class.java)

        // 3) Botón “Tomar foto”
        binding.btnTomarFoto.setOnClickListener {
            // Verificar permiso de cámara
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Pedir permiso
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                abrirCamara()
            }
        }

        // 4) Botón “Elegir de galería”
        binding.btnElegirGaleria.setOnClickListener {
            // Verificar permiso de almacenamiento
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            } else {
                abrirGaleria()
            }
        }

        // 5) Botón “Procesar OCR”
        binding.btnProcesarOCR.setOnClickListener {
            procesarOCR()
        }

        // 6) Botón “Guardar”
        binding.btnGuardarFactura.setOnClickListener {
            guardarFactura(historyId)
        }
    }

    /**
     * Abre la cámara usando Intent y guarda la imagen en un URI temporal.
     */
    private fun abrirCamara() {
        val fotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Crear URI temporal para la foto (podrías usar FileProvider, aquí simplifico):
        val uriTemporal = Uri.fromFile(
            requireContext().cacheDir.resolve("temp_factura_${System.currentTimeMillis()}.jpg")
        )
        imageUri = uriTemporal
        fotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriTemporal)
        takePhotoLauncher.launch(fotoIntent)
    }

    /**
     * Abre la galería para seleccionar imagen.
     */
    private fun abrirGaleria() {
        val galeriaIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickGalleryLauncher.launch(galeriaIntent)
    }

    /**
     * Procesa la imagen con ML Kit para extraer texto y buscar un monto usando regex.
     */
    private fun procesarOCR() {
        val uri = imageUri
        if (uri == null) {
            Toast.makeText(requireContext(), "Debes tomar o seleccionar una foto primero", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Crear InputImage a partir del URI
            val inputImage = InputImage.fromFilePath(requireContext(), uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // Buscar un número con decimales (mínimo “\d+(\.\d{1,2})?”)
                    val regex = Regex("""\d+(\.\d{1,2})?""")
                    val match = regex.find(visionText.text)
                    if (match != null) {
                        // Poner el monto en el campo editable
                        binding.editMontoOCR.setText(match.value)
                    } else {
                        Toast.makeText(requireContext(), "No se detectó monto. Ingresa manualmente.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al procesar OCR", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Guarda la factura en la base de datos: valida que haya URI y monto.
     */
    private fun guardarFactura(historyId: Long) {
        val uri = imageUri
        val montoStr = binding.editMontoOCR.text.toString().trim()

        // Validar
        if (uri == null) {
            Toast.makeText(requireContext(), "No hay imagen para guardar", Toast.LENGTH_SHORT).show()
            return
        }
        if (montoStr.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null) {
            Toast.makeText(requireContext(), "Monto inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto Invoice
        val nuevaFactura = Invoice(
            historyId = historyId,
            rutaImagen = uri.toString(),
            fechaTimestamp = System.currentTimeMillis(),
            monto = monto
        )

        // Insertar en BD
        viewModel.insertInvoice(nuevaFactura)
        Toast.makeText(requireContext(), "Factura guardada", Toast.LENGTH_SHORT).show()
        // Volver a FragmentFacturas
        findNavController().popBackStack()
    }

    // Manejo de permisos (resultado de requestPermissions)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                // Permiso cámara
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara()
                } else {
                    Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
            101 -> {
                // Permiso galería
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirGaleria()
                } else {
                    Toast.makeText(requireContext(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
