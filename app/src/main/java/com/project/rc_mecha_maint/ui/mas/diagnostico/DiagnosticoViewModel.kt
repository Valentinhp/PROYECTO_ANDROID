package com.project.rc_mecha_maint.ui.mas.diagnostico

import androidx.lifecycle.*
import com.project.rc_mecha_maint.data.entity.Failure
import com.project.rc_mecha_maint.data.entity.Symptom
import com.project.rc_mecha_maint.data.repository.DiagnosticoRepository
import kotlinx.coroutines.launch

/**
 * Ahora diagnose() usa MediatorLiveData para esperar a que allFailures cargue.
 */
class DiagnosticoViewModel(private val repo: DiagnosticoRepository) : ViewModel() {

    val allSymptoms: LiveData<List<Symptom>> = repo.getAllSymptoms()
    private val allFailures: LiveData<List<Failure>> = repo.getAllFailures()

    fun diagnose(selectedIds: List<Long>): LiveData<Failure?> {
        val result = MediatorLiveData<Failure?>()
        result.addSource(allFailures) { list ->
            viewModelScope.launch {
                result.postValue(repo.matchFailure(selectedIds, list))
            }
        }
        return result
    }
}

class DiagnosticoViewModelFactory(
    private val repo: DiagnosticoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiagnosticoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiagnosticoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
