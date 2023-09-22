package xyz.chz.bfm.util.magisk

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object MagiskHelper {
    val IS_MAGISK_LITE = "lite" == execRootCmd("magisk -v | grep -o lite")

    fun execRootCmd(cmd: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("su")
            val dos = DataOutputStream(process.outputStream)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            dos.writeBytes("$cmd\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append('\n')
            }
            process.waitFor()
            dos.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result.toString().trim { it <= ' ' }
    }

    fun execRootCmdSilent(cmd: String): Int {
        var result = -1
        try {
            val process = Runtime.getRuntime().exec("su")
            val dos = DataOutputStream(process.outputStream)
            dos.writeBytes("$cmd\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            process.waitFor()
            result = process.exitValue()
            dos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
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