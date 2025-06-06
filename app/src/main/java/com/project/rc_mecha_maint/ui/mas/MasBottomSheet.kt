package com.project.rc_mecha_maint.ui.mas


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.rc_mecha_maint.R
import com.project.rc_mecha_maint.databinding.MasBottomSheetBinding

/**
 * MasBottomSheet: BottomSheet que muestra el menú “Más”.
 *
 * - Infla el XML mas_bottom_sheet.xml (un NavigationView).
 * - Le aplica un shape con esquinas superiores redondeadas.
 * - Al seleccionar cada opción, navega al fragmento correspondiente.
 */
class MasBottomSheet : BottomSheetDialogFragment() {

    // Binding para acceder a las vistas de mas_bottom_sheet.xml
    private var _binding: MasBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MasBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Inflar el menú definido en res/menu/menu_mas.xml
        binding.navMas.inflateMenu(R.menu.menu_mas)

        // 2) Listener para las opciones del menú “Más”
        binding.navMas.setNavigationItemSelectedListener { menuItem ->
            // Cada itemId coincide con los <item android:id="@+id/..." /> de menu_mas.xml
            when (menuItem.itemId) {
                R.id.nav_historial -> {
                    // Ejemplo: navegamos a FragmentHistorial
                    // Si tu FragmentHistorial requiere un argumento “vehicleId”, ponle un valor aquí.
                    val args = Bundle().apply {
                        putLong("vehicleId", 0L) // 0L o el ID que quieras por defecto
                    }
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_historial, args)
                }
                R.id.nav_facturas -> {
                    // Navegar a FragmentFacturas; si necesita “historyId”:
                    val args = Bundle().apply {
                        putLong("historyId", 0L)
                    }
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_facturas, args)
                }
                R.id.nav_costos -> {
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_costos)
                }
                R.id.nav_comparador -> {
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_comparador)
                }
                R.id.nav_talleres -> {
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_talleres)
                }
                R.id.nav_diagnostico -> {
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_diagnostico)
                }
                R.id.nav_perfil -> {
                    requireActivity()
                        .findNavController(R.id.nav_host_fragment_activity_main)
                        .navigate(R.id.nav_perfil)
                }
                else -> {
                    // No debería entrar aquí, pero por si acaso:
                    return@setNavigationItemSelectedListener false
                }
            }
            // 3) Una vez seleccionada una opción, cerramos el BottomSheet
            dismiss()
            true
        }
    }

    /**
     * Override onStart para aplicar el fondo redondeado al contenedor interno
     * del BottomSheet y activar clipToOutline para recortar las esquinas.
     */
    override fun onStart() {
        super.onStart()

        // Esperamos a que la vista se haya layoutado para buscar design_bottom_sheet
        dialog?.window?.decorView?.viewTreeObserver
            ?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // ID interno que Material asigna al contenedor del BottomSheet
                    val bottomSheet =
                        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

                    bottomSheet?.let { sheet ->
                        // 1) Asignar el drawable con esquinas redondeadas
                        sheet.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.shape_bottom_sheet_background
                        )
                        // 2) Habilitar recorte a las esquinas definidas en el drawable
                        sheet.clipToOutline = true
                    }
                    // Quitamos el listener para que no se ejecute más de una vez
                    dialog?.window?.decorView?.viewTreeObserver
                        ?.removeOnGlobalLayoutListener(this)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

