package xyz.chz.bfm.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import xyz.chz.bfm.R
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

fun FloatingActionButton.setMyFab(color: String, res: Int) {
    this.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    this.setImageResource(res)
}

fun toast(str: String, ctx: Context) {
    Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show()
}