package xyz.chz.bfm.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import xyz.chz.bfm.ui.converter.config.ConfigType
import xyz.chz.bfm.util.modul.ModuleManager
import java.net.HttpURLConnection
import java.net.URL


fun ImageView.setImage(res: Int) {
    this.setImageResource(res)
}

fun TextView.moduleVer() {
    this.text = ModuleManager.moduleVersion
}

fun FloatingActionButton.setMyFab(colorAttribute: Int, res: Int) {
    this.backgroundTintList = ColorStateList.valueOf(MaterialColors.getColor(this, colorAttribute))
    this.setImageResource(res)
}

fun toast(str: String, ctx: Context) {
    Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show()
}

fun String.myReplacer(vararg replacements: Pair<Regex, String>): String =
    replacements.fold(this) { acc, (old, new) -> acc.replace(old, new) }

@SuppressLint("ObsoleteSdkInt")
fun setTextHtml(text: String): Spanned {
    val res: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(text)
    }
    return res
}

fun EditText.hideKeyboard(): Boolean {
    return (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showKeyboard(): Boolean {
    return (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, 0)
}

fun EditText.removeEmptyLines(): String {
    //val regex = Regex()
    return this.text.replace("(?m)^[ \t]*\r?\n".toRegex(), "")
}

fun TextView.copyToClipboard(context: Context) {
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText("copied", this.text.toString()))
}

fun EditText.isValidCheck(): Boolean {
    if (this.text.toString().isNotEmpty() || this.text.toString()
            .startsWith(ConfigType.VMESS.scheme) || this.text.toString().startsWith(
            ConfigType.VLESS.scheme
        ) || this.text.toString().startsWith(ConfigType.TROJAN.scheme) || this.text.toString()
            .startsWith(ConfigType.TROJANGO.scheme)
    ) {
        return true
    }
    return false
}

fun String.urlText(): String {
    return URL(this).run {
        openConnection().run {
            this as HttpURLConnection
            inputStream.bufferedReader().readText().trim()
        }
    }
}