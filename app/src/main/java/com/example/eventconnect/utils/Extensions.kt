package com.example.eventconnect.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.Date

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}

// Extension pour formater les dates
fun Date.toFormattedString(): String {
    // Implémentation de formatage de date
    return toString()
}