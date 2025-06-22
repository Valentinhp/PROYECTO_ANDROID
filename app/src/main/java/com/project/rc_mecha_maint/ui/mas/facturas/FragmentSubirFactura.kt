package com.project.rc_mecha_maint.ui.mas.facturas

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import java.text.Normalizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Invoice
import com.project.rc_mecha_maint.databinding.FragmentSubirFacturaBinding
import kotlinx.coroutines.launch
import java.io.File

class FragmentSubirFactura : Fragment() {

    private var _binding: FragmentSubirFacturaBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private var historyId: Long = 0L
    private lateinit var talleresNombres: List<String>

    private val permisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) abrirCamara() else toast("Permiso de cámara denegado") }

    private val fotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok -> if (ok) mostrarImagen() else toast("No se tomó la foto") }

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it; mostrarImagen() } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubirFacturaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        historyId = arguments?.getLong("historyId") ?: 0L

        // Spinner de categorías (añadido "Mantenimiento")
        val categorias = listOf(
            "Aceite", "Frenos", "Motor",
            "Suspensión", "Llantas", "Mantenimiento", "Otro"
        )
        binding.spinnerCategoria.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)

        lifecycleScope.launch {
            val dao      = AppDatabase.getInstance(requireContext()).workshopDao()
            val talleres = dao.getAllSync()
            talleresNombres = talleres.map { it.nombre }

            binding.spinnerTaller.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, talleresNombres)

            binding.btnGuardarFactura.setOnClickListener { guardarFactura(talleres) }
        }

        binding.btnTomarFoto.setOnClickListener { pedirPermisoOCamara() }
        binding.btnElegirGaleria.setOnClickListener { galeriaLauncher.launch("image/*") }
        binding.btnProcesarOCR.setOnClickListener { procesarOCR() }
    }

    private fun pedirPermisoOCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) abrirCamara()
        else permisoCamara.launch(Manifest.permission.CAMERA)
    }

    private fun abrirCamara() {
        val archivo  = File(requireContext().cacheDir, "fact_${System.currentTimeMillis()}.jpg")
        imageUri     = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", archivo
        )
        fotoLauncher.launch(imageUri)
    }

    private fun mostrarImagen() = try {
        requireContext().contentResolver.openInputStream(imageUri).use { stream ->
            binding.imagePreview.setImageBitmap(BitmapFactory.decodeStream(stream))
        }
    } catch (e: Exception) {
        toast("Error al cargar imagen")
    }

    private fun procesarOCR() {
        // ← Validación añadida para evitar OCR sin imagen
        if (!this::imageUri.isInitialized) {
            toast("Agrega primero una imagen para procesar OCR")
            return
        }

        try {
            val img     = InputImage.fromFilePath(requireContext(), imageUri)
            val client  = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            client.process(img)
                .addOnSuccessListener { res ->
                    val rawText = res.text

                    // Normalizar texto alfabético (no tocamos estrellas ni fecha)
                    val textoPlano = Normalizer
                        .normalize(rawText, Normalizer.Form.NFD)
                        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                        .lowercase()

                    val lineas = textoPlano.split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    fun idxOf(pref: String) = lineas.indexOfFirst { it.startsWith(pref) }
                    fun lineaRegex(regex: Regex) =
                        lineas.firstNotNullOfOrNull { ln -> regex.find(ln) }

                    // ----- Fecha -----
                    Regex("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})").find(rawText)
                        ?.groupValues?.get(1)
                        ?.let { dateStr ->
                            binding.editFechaOCR.setText(dateStr)
                        }

                    // ----- Monto -----
                    Regex("(total|importe|monto|€|\\$)[^\\d]*(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2}))")
                        .find(textoPlano)
                        ?.groupValues?.get(2)
                        ?.let { raw ->
                            binding.editMontoOCR.setText(raw.replace(".", "").replace(",", "."))
                        }

                    // ----- Concepto -----
                    idxOf("concepto").takeIf { it >= 0 && it + 1 < lineas.size }?.let { i ->
                        binding.editConcepto.setText(
                            lineas[i + 1].replaceFirstChar { c -> c.uppercase() }
                        )
                    }

                    // ----- Categoría -----
                    idxOf("categoria").takeIf { it >= 0 && it + 1 < lineas.size }?.let { i ->
                        val valorNorm = lineas[i + 1].lowercase()
                        val adapter    = binding.spinnerCategoria.adapter
                        (0 until adapter.count)
                            .firstOrNull { idx ->
                                (adapter.getItem(idx) as String).lowercase() == valorNorm
                            }
                            ?.let { binding.spinnerCategoria.setSelection(it) }
                    }

                    // ----- Taller -----
                    idxOf("taller").takeIf { it >= 0 && it + 1 < lineas.size }?.let { i ->
                        val raw = lineas[i + 1]
                        val nomSolo = raw.replace(Regex("[★☆*]"), "").trim()
                        talleresNombres.indexOfFirst { it.equals(nomSolo, true) }
                            .takeIf { it >= 0 }
                            ?.let { binding.spinnerTaller.setSelection(it) }
                    }

                    // ----- Estrellas -----
                    val blackStars = rawText.count { it == '★' || it == '*' }
                    val whiteStars = rawText.count { it == '☆' }
                    val rating = when {
                        blackStars in 1..5 -> blackStars
                        whiteStars in 1..5 -> 5 - whiteStars
                        else -> null
                    }
                    rating?.let {
                        binding.ratingBar.numStars = 5
                        binding.ratingBar.stepSize = 1f
                        binding.ratingBar.rating   = it.toFloat()
                    }

                    toast("Campos rellenados. Revisa y guarda.")
                }
                .addOnFailureListener { toast("Error OCR") }
        } catch (e: Exception) {
            toast("OCR falló")
        }
    }

    private fun guardarFactura(talleres: List<com.project.rc_mecha_maint.data.entity.Workshop>) {
        // ← Validación añadida para evitar crash sin imagen
        if (!this::imageUri.isInitialized) {
            toast("Debes agregar una imagen de la factura")
            return
        }

        val concepto  = binding.editConcepto.text.toString().trim()
        val monto     = binding.editMontoOCR.text.toString().replace(",", ".").toDoubleOrNull()
        val fecha     = binding.editFechaOCR.text.toString().trim()
        val idxTaller = binding.spinnerTaller.selectedItemPosition

        if (concepto.isEmpty() || monto == null || fecha.isEmpty() || idxTaller < 0) {
            toast("Completa los campos obligatorios")
            return
        }

        val factura = Invoice(
            historyId      = historyId,
            rutaImagen     = imageUri.toString(),
            fechaTimestamp = System.currentTimeMillis(),
            monto          = monto,
            concepto       = concepto,
            categoria      = binding.spinnerCategoria.selectedItem.toString(),
            tallerId       = talleres[idxTaller].id,
            calificacion   = binding.ratingBar.rating.toInt().takeIf { it > 0 },
            esCotizacion   = binding.checkCotizacion.isChecked,
            vehicleId      = null
        )

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).invoiceDao().insert(factura)
            toast("Factura guardada")
            findNavController().popBackStack()
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
