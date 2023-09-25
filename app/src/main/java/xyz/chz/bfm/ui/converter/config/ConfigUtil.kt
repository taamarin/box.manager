package xyz.chz.bfm.ui.converter.config

import android.util.Base64

object ConfigUtil {

    fun decode(text: String): String {
        tryDecodeBase64(text)?.let { return it }
        if (text.endsWith('=')) {
            // try again for some loosely formatted base64
            tryDecodeBase64(text.trimEnd('='))?.let { return it }
        }
        return ""
    }

    private fun tryDecodeBase64(text: String): String? {
        try {
            return Base64.decode(text, Base64.NO_WRAP).toString(charset("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            return Base64.decode(text, Base64.NO_WRAP.or(Base64.URL_SAFE))
                .toString(charset("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}