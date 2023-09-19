package xyz.chz.bfm.util

import android.os.Handler
import android.os.Looper
import xyz.chz.bfm.util.command.TermCmd

object Util {
    private val handler = Handler(Looper.getMainLooper())

    fun runOnUiThread(action: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.invoke()
        }
    }

    var isProxyed = TermCmd.isProxying()
}