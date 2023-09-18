package xyz.chz.bfm.util

import android.graphics.Color
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import xyz.chz.bfm.util.modul.ModuleManager


fun MaterialCardView.setColorBackground(str: String) {
    this.setCardBackgroundColor(Color.parseColor(str))
    this.radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics)
}

fun ImageView.setImage(res: Int) {
    this.setImageResource(res)
}

fun TextView.moduleVer() {
    this.text = ModuleManager.moduleVersion
}