package com.project.rc_mecha_maint.ui.mas.facturas

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.data.repository.InvoiceRepository
import com.project.rc_mecha_maint.ui.mas.facturas.InvoiceViewModel
import com.project.rc_mecha_maint.ui.mas.facturas.InvoiceViewModelFactory
import java.io.File
import java.util.regex.Pattern

class FragmentSubirFactura : Fragment() {

    // 1) ViewModel y argumento historyId
    private lateinit var viewModel: InvoiceViewModel
    private var historyId: Long = 0L

    // 2) Vistas
    private lateinit var imagePreview: ImageView
    private lateinit var btnTomarFoto: Button
    private lateinit var btnElegirGaleria: Button
    private lateinit var btnProcesarOCR: Button
    private lateinit var editMontoOCR: TextInputEditText
    private lateinit var btnGuardarFactura: Button

    private lateinit var imageUri: Uri

    // 3) Lanzador de permisos de cámara
    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(requireContext(),
            "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // 4) Lanzador para tomar foto
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) mostrarImagen()
        else Toast.makeText(requireContext(),
            "No se tomó la foto", Toast.LENGTH_SHORT).show()
    }

    // 5) Lanzador para elegir imagen de galería
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            mostrarImagen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_subir_factura, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 6) Inicializa ViewModel con historyId
        historyId = arguments?.getLong(FragmentFacturas.ARG_HISTORY_ID) ?: 0L
        val dao  = AppDatabase.getInstance(requireContext()).invoiceDao()
        val repo = InvoiceRepository(dao)
        viewModel = ViewModelProvider(
            this,
            InvoiceViewModelFactory(repo)
        )[InvoiceViewModel::class.java]

        // 7) Conecta vistas
        imagePreview      = view.findViewById(R.id.imagePreview)
        btnTomarFoto      = view.findViewById(R.id.btnTomarFoto)
        btnElegirGaleria  = view.findViewById(R.id.btnElegirGaleria)
        btnProcesarOCR    = view.findViewById(R.id.btnProcesarOCR)
        editMontoOCR      = view.findViewById(R.id.editMontoOCR)
        btnGuardarFactura = view.findViewById(R.id.btnGuardarFactura)

        // 8) Botones iniciales
        btnProcesarOCR.isEnabled    = false
        btnGuardarFactura.isEnabled = false

        // 9) Listeners
        btnTomarFoto.setOnClickListener { pedirPermisoOCamara() }
        btnElegirGaleria.setOnClickListener { galeriaLauncher.launch("image/*") }
        btnProcesarOCR.setOnClickListener { procesarOCR() }
        btnGuardarFactura.setOnClickListener { guardarFactura() }
    }

    // -- Gestión de permisos y cámara --

    private fun pedirPermisoOCamara() {
        val permiso = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        )
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        // Nombre único para el archivo
        val nombreArchivo = "factura_${System.currentTimeMillis()}.jpg"
        val archivo = File(requireContext().cacheDir, nombreArchivo)

        // -----------------------------------
        // Aquí va LA LÍNEA CLAVE CORREGIDA:
        // -----------------------------------
        val authority = "${requireContext().packageName}.fileprovider"
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            authority,   // ¡debe coincidir con AndroidManifest!
            archivo
        )

        tomarFotoLauncher.launch(imageUri)
    }

    private fun mostrarImagen() {
        try {
            requireContext().contentResolver.openInputStream(imageUri).use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                imagePreview.setImageBitmap(bmp)
            }
            btnProcesarOCR.isEnabled    = true
            btnGuardarFactura.isEnabled = true
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // -- Procesamiento OCR --

    private fun procesarOCR() {
        try {
            val imagen = InputImage.fromFilePath(requireContext(), imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(imagen)
                .addOnSuccessListener { resultado ->
                    val texto = resultado.text
                    val patrón = Pattern.compile("\\d+[.,]?\\d*")
                    val matcher = patrón.matcher(texto)
                    if (matcher.find()) {
                        val monto = matcher.group().replace(",", ".")
                        editMontoOCR.setText(monto)
                        Toast.makeText(requireContext(),
                            "Monto detectado: $monto", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),
                            "No se encontró monto", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),
                        "Error en OCR", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "No se puede procesar OCR", Toast.LENGTH_SHORT).show()
        }
    }

    // -- Guardar factura en BD --

    private fun guardarFactura() {
        val textoMonto = editMontoOCR.text.toString().trim()
        if (textoMonto.isEmpty()) {
            Toast.makeText(requireContext(),
                "Ingresa el monto antes de guardar", Toast.LENGTH_SHORT).show()
            return
        }
        val montoDouble = textoMonto.replace(",", ".").toDoubleOrNull() ?: 0.0
        val nuevaFactura = Invoice(
            historyId      = historyId,
            rutaImagen     = imageUri.toString(),
            fechaTimestamp = System.currentTimeMillis(),
            monto          = montoDouble
        )
        viewModel.insertInvoice(nuevaFactura)
        findNavController().popBackStack()
    }
}
