package xyz.chz.bfm.util.terminal

import android.util.Log
import xyz.chz.bfm.BuildConfig

object TerminalHelper {
    private const val TAG = "BoxForRoot.Terminal"

    fun execRootCmd(cmd: String): String {
        return try {
            val process: Process = Runtime.getRuntime().exec("su -c $cmd")
            process.waitFor()
            val output = process.inputStream.bufferedReader().lineSequence().joinToString("\n")
            if (BuildConfig.DEBUG) Log.d(TAG, output)
            output
        } catch (e: Exception) {
            ""
        }
    }

    fun execRootCmdSilent(cmd: String): Int {
        return try {
            val process: Process = Runtime.getRuntime().exec("su -c $cmd")
            process.waitFor()
            process.exitValue()
        } catch (e: Exception) {
            -1
        }
    }

    fun execRootCmdVoid(cmd: String, callback: (Boolean) -> Unit) {
        try {
            val process = Runtime.getRuntime().exec("su -c $cmd")
            process.waitFor()
            callback(process.exitValue() == 0)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)
        }
    }
}