package com.project.rc_mecha_maint

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.project.rc_mecha_maint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Referencia al BottomNavigationView
        val navView: BottomNavigationView = binding.navView

        // NavController apuntando al NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Cada uno de estos IDs coincide con los fragmentos principales en nav_graph.xml
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes
            )
        )

        // Configuramos la ActionBar para que se sincronice con el NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Hacemos que el BottomNavigationView navegue con el NavController
        navView.setupWithNavController(navController)
    }
}
