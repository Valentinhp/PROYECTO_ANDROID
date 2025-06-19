// app/src/main/java/com/project/rc_mecha_maint/MainActivity.kt
package com.project.rc_mecha_maint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.rc_mecha_maint.data.AppDatabase
import com.project.rc_mecha_maint.data.entity.Failure
import com.project.rc_mecha_maint.data.entity.Symptom
import com.project.rc_mecha_maint.data.entity.Workshop
import com.project.rc_mecha_maint.databinding.ActivityMainBinding
import com.project.rc_mecha_maint.ui.mas.MasBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 0) Inflar layout con ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Sembrar datos de diagnóstico y talleres
        seedFromAssets()

        // 2) Configurar toolbar como ActionBar
        setSupportActionBar(binding.toolbar)

        // 3) Configurar NavController y AppBarConfiguration
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes
            )
        )
        setupActionBarWithNavController(navController, appBarConfig)

        // 4) Manejar clicks del BottomNavigationView manualmente
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // "Más": abre el BottomSheet
                R.id.nav_mas -> {
                    MasBottomSheet().show(supportFragmentManager, "MasBottomSheet")
                    true
                }
                // Resto de pestañas: navega al fragmento correspondiente
                R.id.nav_inicio,
                R.id.nav_vehiculos,
                R.id.nav_recordatorios,
                R.id.nav_reportes -> {
                    navController.navigate(item.itemId)
                    true
                }
                else -> false
            }
        }
    }

    // Habilita la flecha "Up" en la toolbar
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Lee failures.json y talleres.json desde assets,
     * e inserta síntomas, fallas y talleres en Room.
     */
    private fun seedFromAssets() {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // — Failures & Symptoms —
                val failuresText = assets.open("failures.json")
                    .bufferedReader()
                    .use { it.readText() }
                val root = JSONObject(failuresText)

                // Symptoms
                val syArr = root.getJSONArray("symptoms")
                val symptoms = List(syArr.length()) { i ->
                    val o = syArr.getJSONObject(i)
                    Symptom(
                        id        = o.getLong("id"),
                        nombre    = o.getString("nombre"),
                        categoria = o.getString("categoria")
                    )
                }
                db.symptomDao().insertAll(symptoms)

                // Failures
                val faArr = root.getJSONArray("failures")
                val failures = List(faArr.length()) { i ->
                    val o = faArr.getJSONObject(i)
                    Failure(
                        id            = o.getLong("id"),
                        nombreFalla   = o.getString("nombreFalla"),
                        descripcion   = o.getString("descripcion"),
                        recomendacion = o.getString("recomendacion"),
                        sintomasJSON  = o.getJSONArray("sintomas").toString()
                    )
                }
                db.failureDao().insertAll(failures)

                // — Talleres —
                val talleresText = assets.open("workshops.json")
                    .bufferedReader()
                    .use { it.readText() }
                val talleresType = object : TypeToken<List<Workshop>>() {}.type
                val talleres: List<Workshop> = Gson().fromJson(talleresText, talleresType)
                db.workshopDao().insertAll(talleres)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
