package com.project.rc_mecha_maint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.rc_mecha_maint.databinding.ActivityMainBinding
import com.project.rc_mecha_maint.ui.mas.MasBottomSheet

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Referencia al BottomNavigationView
        val navView: BottomNavigationView = binding.navView

        // 2) NavController apuntando al NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // 3) IDs de los cuatro fragmentos principales
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes
            )
        )

        // 4) Sincronizar la ActionBar con NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 5) Interceptar pulsaciones en el BottomNavigationView
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mas -> {
                    // Si el usuario pulsa “Más”, abrimos el BottomSheet
                    MasBottomSheet().show(supportFragmentManager, "MasBottomSheet")
                    true
                }
                else -> {
                    // Para los demás ítems (Inicio, Vehículos, Recordatorios, Reportes),
                    // delegamos a NavController para la navegación normal
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }

    // 6) Habilitar el botón “Up” (flecha atrás en la ActionBar)
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
