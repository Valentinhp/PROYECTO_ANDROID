package com.project.rc_mecha_maint.data.repository

import com.project.rc_mecha_maint.data.dao.FailureDao
import com.project.rc_mecha_maint.data.dao.SymptomDao
import com.project.rc_mecha_maint.data.entity.Failure
import com.project.rc_mecha_maint.data.entity.Symptom
import org.json.JSONArray

/**
 * Repositorio para lógica de diagnóstico.
 */
class DiagnosticoRepository(
    private val symptomDao: SymptomDao,
    private val failureDao: FailureDao
) {
    fun getAllSymptoms() = symptomDao.getAllSymptoms()
    fun getAllFailures() = failureDao.getAllFailures()

    /**
     * Retorna la primera falla que contenga todos los selectedIds.
     */
    fun matchFailure(selectedIds: List<Long>, allFailures: List<Failure>): Failure? {
        for (f in allFailures) {
            val arr = JSONArray(f.sintomasJSON)
            val ids = mutableListOf<Long>()
            for (i in 0 until arr.length()) ids += arr.getLong(i)
            if (selectedIds.all { it in ids }) return f
        }
        return null
    }
}
