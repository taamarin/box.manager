package xyz.chz.bfm.util

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import xyz.chz.bfm.util.modul.ModuleManager


fun MaterialCardView.setColorBackground(str: String) {
    this.setBackgroundColor(Color.parseColor(str))
}

fun ImageView.setImage(res: Int) {
    this.setImageResource(res)
}

fun TextView.moduleVer() {
    this.text = ModuleManager.moduleVersion
}