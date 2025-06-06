package com.project.rc_mecha_maint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.rc_mecha_maint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Referencia al BottomNavigationView
        val navView: BottomNavigationView = binding.navView

        // 2) NavController apuntando al NavHostFragment (ID coincide con activity_main.xml)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // 3) IDs de los fragmentos principales definidos en nav_graph.xml
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes
            )
        )

        // 4) Sincronizar la barra de acción con el NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 5) Hacer que el BottomNavigationView funcione con el NavController
        navView.setupWithNavController(navController)
    }

    // 6) Habilitar el botón "Up" (flecha atrás en el ActionBar)
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
