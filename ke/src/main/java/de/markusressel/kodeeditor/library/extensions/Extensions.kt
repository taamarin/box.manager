package de.markusressel.kodeeditor.library.extensions

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

/**
 * Get a color from this TypedArray or use the first default that is found
 *
 * @param context view context
 * @param defaultColor default if none of the styleable or attribute values was found
 * @param styleableRes styleable resource
 * @param attr theme attribute resource
 */
@ColorInt
fun TypedArray.getColor(context: Context, @ColorInt defaultColor: Int = Color.BLACK, @StyleableRes styleableRes: Int, @AttrRes vararg attr: Int): Int {
    return getColor(styleableRes, attr.find { context.getThemeAttrColor(it) != null }
            ?: defaultColor)
}

/**
 * Get Color from Theme attribute
 *
 * @param attr    Attribute resource ID
 * @return Color as Int
 */
@ColorInt
fun Context.getThemeAttrColor(@AttrRes attr: Int): Int? {
    val typedValue = TypedValue()
    if (theme.resolveAttribute(attr, typedValue, true)) {
        if (typedValue.type >= TypedValue.TYPE_FIRST_INT && typedValue.type <= TypedValue.TYPE_LAST_INT) {
            return typedValue.data
        } else if (typedValue.type == TypedValue.TYPE_STRING) {
            return ContextCompat.getColor(this, typedValue.resourceId)
        }
    }

    return null
}

/**
 * Sets a view background without resetting it's padding
 *
 * @param background the background drawable to use (may be null)
 */
fun View.setViewBackgroundWithoutResettingPadding(background: Drawable?) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    ViewCompat.setBackground(this, background)
    ViewCompat.setPaddingRelative(this, paddingStart, paddingTop, paddingEnd, paddingBottom)
}

/**
 * Converts the given number to a px value assuming it is a dp value.
 *
 * @return px value
 */
fun Number.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics)
}

/**
 * Renders a view to a bitmap
 *
 * @param dimensionLimit the maximum image dimension
 * @param backgroundColor background color in case the view doesn't have one
 * @return the rendered image or null if the view has no measured dimensions (yet)
 */
fun View.createSnapshot(dimensionLimit: Number = 1F, backgroundColor: Int = Color.TRANSPARENT): Bitmap? {
    if (measuredWidth == 0 || measuredHeight == 0) {
        // the view has no dimensions so it can't be rendered
        return null
    }

    val limitAsFloat = dimensionLimit.toFloat()

    // select smaller scaling factor to match dimensionLimit
    val scaleFactor = Math.min(
            Math.min(
                    limitAsFloat / measuredWidth,
                    limitAsFloat / measuredHeight),
            1F)

    // Define a bitmap with the target dimensions
    val returnedBitmap = Bitmap.createBitmap(
            (this.measuredWidth * scaleFactor).toInt(),
            (this.measuredHeight * scaleFactor).toInt(),
            Bitmap.Config.ARGB_8888)

    // bind a canvas to the bitmap
    Canvas(returnedBitmap).apply {
        scale(scaleFactor, scaleFactor)
        drawColor(backgroundColor)
        background?.draw(this)
        draw(this)
    }

    return returnedBitmap
}
