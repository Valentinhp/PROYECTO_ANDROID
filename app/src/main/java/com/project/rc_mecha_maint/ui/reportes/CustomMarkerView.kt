package com.project.rc_mecha_maint.ui.reportes

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.project.rc_mecha_maint.R

class CustomMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {

    private val tvMarker: TextView = findViewById(R.id.tvMarker)

    // Este método se llama cada vez que se toca un punto
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?. let {
            tvMarker.text = when {
                highlight?.dataSetIndex == null -> String.format("$%.2f", it.y)
                else -> String.format("$%.2f", it.y)
            }
        }
        super.refreshContent(e, highlight)
    }

    // Ajusta la posición del MarkerView para que aparezca centrado arriba del punto
    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
