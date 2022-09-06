package com.example.carrier_pigeon.app.utils

import android.view.View

// Visibility functions
fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

// Enabled functions
fun View?.enable() {
    this?.isEnabled = true
}

fun View?.disable() {
    this?.isEnabled = false
}

// Clickable functions
fun View?.clickable() {
    this?.isClickable = true
}

fun View?.unclickable() {
    this?.isClickable = false
}
