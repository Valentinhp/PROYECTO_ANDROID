package com.project.rc_mecha_maint.util

import android.content.Context
import android.widget.Toast

/**
 * Extensión para mostrar Toast fácilmente.
 */
fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
