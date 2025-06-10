package com.project.rc_mecha_maint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.appbar.MaterialToolbar
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Failure
import com.project.rc_mecha_maint.data.entity.Symptom
import com.project.rc_mecha_maint.databinding.ActivityMainBinding
import com.project.rc_mecha_maint.ui.mas.MasBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 0) Inflar el layout con ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Sembrar datos de diagnóstico desde assets/failures.json
        seedFromAssets()

        // 2) Configurar la Toolbar como ActionBar
        //    a) Encuentra el view <MaterialToolbar> en el binding (debe tener android:id="@+id/toolbar")
        val toolbar = binding.toolbar as MaterialToolbar
        //    b) Indica a AppCompatActivity que use este toolbar
        setSupportActionBar(toolbar)

        // 3) Configurar NavController y AppBarConfiguration
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        //    Los IDs de los fragments que son raíz (no muestran flecha Up)
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes
            )
        )
        //    Conecta la ActionBar (toolbar) con el NavController
        setupActionBarWithNavController(navController, appBarConfig)

        // 4) BottomNavigationView: manejar clicks
        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mas -> {
                    MasBottomSheet().show(supportFragmentManager, "MasBottomSheet")
                    true
                }
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }

    // Habilita la flecha “Up” en la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Lee assets/failures.json y carga síntomas y fallas en Room
     */
    private fun seedFromAssets() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val text = assets.open("failures.json")
                    .bufferedReader()
                    .use { it.readText() }
                val root = JSONObject(text)

                // Crea lista de Symptom
                val syArr = root.getJSONArray("symptoms")
                val symptoms = List(syArr.length()) { i ->
                    val o = syArr.getJSONObject(i)
                    Symptom(
                        id        = o.getLong("id"),
                        nombre    = o.getString("nombre"),
                        categoria = o.getString("categoria")
                    )
                }

                // Crea lista de Failure
                val faArr = root.getJSONArray("failures")
                val failures = List(faArr.length()) { i ->
                    val o = faArr.getJSONObject(i)
                    val sintomasJSON = o.getJSONArray("sintomas").toString()
                    Failure(
                        id            = o.getLong("id"),
                        nombreFalla   = o.getString("nombreFalla"),
                        descripcion   = o.getString("descripcion"),
                        recomendacion = o.getString("recomendacion"),
                        sintomasJSON  = sintomasJSON
                    )
                }

                // Inserta (o reemplaza) en la base
                db.symptomDao().insertAll(symptoms)
                db.failureDao().insertAll(failures)

            } catch (e: Exception) {
                e.printStackTrace() // Depuración en caso de error
            }
        }
    }
}
