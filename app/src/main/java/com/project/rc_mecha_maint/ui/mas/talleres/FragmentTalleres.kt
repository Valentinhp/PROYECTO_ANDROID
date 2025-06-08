package com.project.rc_mecha_maint.ui.mas.talleres

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.rc_mecha_maint.databinding.FragmentTalleresBinding
import com.project.rc_mecha_maint.data.entity.Workshop

class FragmentTalleres : Fragment() {

    // ───────────────────────── ViewBinding ──────────────────────────
    private var _b: FragmentTalleresBinding? = null
    private val b get() = _b!!

    // ───────────────────────── ViewModel ────────────────────────────
    private val vm by activityViewModels<WorkshopViewModel>()

    // ───────────────────────── Adapter ──────────────────────────────
    private lateinit var adapter: WorkshopAdapter

    // ───────────────────────── Ciclo de vida ────────────────────────
    override fun onCreateView(
        i: LayoutInflater, c: ViewGroup?, s: Bundle?
    ) = FragmentTalleresBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        // 1) Configura RecyclerView
        adapter = WorkshopAdapter(::dial, ::maps, ::editOrDetail)
        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        // 2) Observa datos
        vm.workshops.observe(viewLifecycleOwner) { adapter.submitList(it) }

        // 3) FAB → alta
        b.fabAdd.setOnClickListener {
            AddEditTallerDialog.nuevaInstancia(null)
                .show(parentFragmentManager, "add")
        }

        // 4) Swipe-to-delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                vm.delete(adapter.currentList[vh.adapterPosition])
            }
        }).attachToRecyclerView(b.recycler)
    }

    // ───────────────────────── Acciones de ítem ─────────────────────
    /** Llamar al taller */
    private fun dial(w: Workshop) =
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${w.telefono}")))

    /** Abrir ubicación en Google Maps */
    private fun maps(w: Workshop) {
        val geo = "geo:${w.latitud},${w.longitud}?q=${Uri.encode(w.nombre)}"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geo)))
    }

    /**
     * Tap sobre card:
     *    • si quieres solo ver el detalle → navega
     *    • si quieres editar → abre diálogo
     * Aquí lo hacemos EDITAR (se puede cambiar a gusto)
     */
    private fun editOrDetail(w: Workshop) {
        AddEditTallerDialog.nuevaInstancia(w)
            .show(parentFragmentManager, "edit")
        // Si prefieres navegar al detalle usa:
        // val act = FragmentTalleresDirections.actionTalleresToDetalleTaller(w.id)
        // findNavController().navigate(act)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
