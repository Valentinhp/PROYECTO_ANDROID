package com.project.rc_mecha_maint.ui.mas.perfil

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.UserProfile
import com.project.rc_mecha_maint.data.repository.UserProfileRepository
import com.project.rc_mecha_maint.databinding.FragmentPerfilBinding
import com.project.rc_mecha_maint.util.toast

class FragmentPerfil : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserProfileViewModel
    private var currentProfile: UserProfile? = null
    private var imageUri: Uri? = null

    // Launcher para abrir el picker y obtener un documento (imagen)
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { contentUri ->
            // Guardar permiso persistente (solo funciona con URIs persistibles)
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* no persistible, OK */ }

            imageUri = contentUri
            // Mostrar inmediatamente en el formulario
            requireContext().contentResolver.openInputStream(contentUri)?.use { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                binding.imgFoto.setImageBitmap(bmp)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // Inicializar ViewModel
        val dao     = AppDatabase.getInstance(requireContext()).userProfileDao()
        val repo    = UserProfileRepository(dao)
        val factory = UserProfileViewModelFactory(repo)
        viewModel   = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        // Observar perfil en la base de datos
        viewModel.user.observe(viewLifecycleOwner) { perfil ->
            currentProfile = perfil
            if (perfil != null) showDetails(perfil)
            else showForm()
        }

        // Seleccionar foto
        binding.btnSelectFoto.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        // Editar perfil existente
        binding.btnEditar.setOnClickListener {
            showForm()
            currentProfile?.let { fillForm(it) }
        }

        // Guardar o actualizar perfil
        binding.btnGuardar.setOnClickListener { saveProfile() }

        // Cerrar sesión / eliminar perfil
        binding.btnCerrarSesion.setOnClickListener {
            viewModel.deleteUser()
            requireContext().toast("Sesión cerrada")
        }

        return binding.root
    }

    private fun showDetails(perfil: UserProfile) {
        binding.detailsCard.visibility = View.VISIBLE
        binding.formCard.visibility    = View.GONE

        // Cargo la foto leyendo el stream directamente
        if (perfil.fotoURI.isNotEmpty()) {
            val uri = Uri.parse(perfil.fotoURI)
            try {
                requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    binding.imgFotoRead.setImageBitmap(bmp)
                }
            } catch (_: Exception) {
                binding.imgFotoRead.setImageDrawable(null)
            }
        } else {
            binding.imgFotoRead.setImageDrawable(null)
        }

        binding.tvNombreRead.text   = perfil.nombre
        binding.tvCorreoRead.text   = perfil.correo
        binding.tvTelefonoRead.text = perfil.telefono
        binding.tvRolRead.text      = perfil.rol
    }

    private fun showForm() {
        binding.detailsCard.visibility = View.GONE
        binding.formCard.visibility    = View.VISIBLE
    }

    private fun fillForm(perfil: UserProfile) {
        // Prellenar formulario
        imageUri = perfil.fotoURI.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
        imageUri?.let { uri ->
            try {
                requireContext().contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    binding.imgFoto.setImageBitmap(bmp)
                }
            } catch (_: Exception) { /* si falla, dejamos la default */ }
        }
        binding.etNombre.setText(perfil.nombre)
        binding.etCorreo.setText(perfil.correo)
        binding.etTelefono.setText(perfil.telefono)
        if (perfil.rol == "DUEÑO") binding.rbDueno.isChecked = true
        else binding.rbTaller.isChecked = true
    }

    private fun saveProfile() {
        val nombre = binding.etNombre.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        if (nombre.isEmpty() || correo.isEmpty()) {
            requireContext().toast("Nombre y correo obligatorios")
            return
        }
        val telefono = binding.etTelefono.text.toString().trim()
        val rol      = if (binding.rbDueno.isChecked) "DUEÑO" else "TALLER"

        val perfil = UserProfile(
            id       = currentProfile?.id ?: 0L,
            nombre   = nombre,
            correo   = correo,
            telefono = telefono,
            rol      = rol,
            fotoURI  = imageUri?.toString().orEmpty()
        )
        viewModel.saveProfile(perfil)
        requireContext().toast("Perfil guardado")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
