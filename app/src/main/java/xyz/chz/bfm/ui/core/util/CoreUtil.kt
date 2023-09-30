package xyz.chz.bfm.ui.core.util

import xyz.chz.bfm.util.terminal.TerminalHelper
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream

object CoreUtil {

    fun gZipExtractor(gzipIn: String, gzipOut: String) {
        try {
            val fis = FileInputStream(gzipIn)
            val gis = GZIPInputStream(fis)
            val fos = FileOutputStream(gzipOut)
            val buff = ByteArray(1024)
            var len: Int
            while (gis.read(buff).also { len = it } != -1) {
                fos.write(buff, 0, len)
            }
            fos.close()
            gis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun tarExtractUsingRoot(tarIn: String, tarOut: String) {
        TerminalHelper.execRootCmdSilent("tar xfz $tarIn -C $tarOut && mv -f $tarOut/sing-box-v* $tarOut/sing-box && chmod 755 $tarOut/sing-box && chown root:net_admin $tarOut/sing-box")
    }

    fun getAbis(): String {
        for (abis in android.os.Build.SUPPORTED_ABIS) {
            when (abis) {
                "arm64-v8a" -> return "arm64"
                "armeabi-v7a" -> return "armv7"
            }
        }
        return throw RuntimeException("unable get abis")
    }
}