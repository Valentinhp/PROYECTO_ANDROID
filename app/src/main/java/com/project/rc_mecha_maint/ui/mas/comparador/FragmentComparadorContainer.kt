package com.project.rc_mecha_maint.ui.mas.comparador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.project.rc_mecha_maint.databinding.FragmentComparadorBinding
import com.project.rc_mecha_maint.ui.mas.talleres.FragmentTalleres

class FragmentComparadorContainer : Fragment() {
    private var _b: FragmentComparadorBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentComparadorBinding.inflate(inflater, container, false)
        .also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(pos: Int) = when (pos) {
                0 -> FragmentTalleres()
                else -> FragmentAutopartes()
            }
        }
        TabLayoutMediator(b.tabLayout, b.viewPager) { tab, pos ->
            tab.text = if (pos == 0) "Talleres" else "Autopartes"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
