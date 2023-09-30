package xyz.chz.bfm.ui.core.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import xyz.chz.bfm.util.command.CoreCmd
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class DownloaderCore(private val ctx: Context) {
    private var exFile: File? = null

    fun downloadFile(
        stringUrl: String,
        nameFile: String,
        path: String,
        callback: IDownloadCore
    ) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            handler.post { callback.onDownloadingStart() }
            try {
                val url = URL(stringUrl)
                val conection = url.openConnection()
                conection.connect()
                val lenghtOfFile = conection.contentLength
                val input: InputStream = BufferedInputStream(url.openStream(), 8192)
                exFile = File(ctx.getExternalFilesDir(null), nameFile)
                if (exFile!!.exists()) exFile!!.delete()
                val output = FileOutputStream(exFile)
                val data = ByteArray(1024)
                var total: Long = 0
                while (true) {
                    val read = input.read(data)
                    if (read == -1) {
                        break
                    }
                    output.write(data, 0, read)
                    val j2 = total + read.toLong()
                    if (lenghtOfFile > 0) {
                        val progress = ((100 * j2 / lenghtOfFile.toLong()).toInt())
                        callback.onDownloadingProgress(progress)
                    }
                    total = j2
                }
                output.flush()
                output.close()
                input.close()
                handler.post { callback.onDownloadingComplete() }
                if (nameFile.contains("clash")) {
                    val x = exFile.toString().replace(".gz", "")
                    CoreUtil.gZipExtractor(
                        exFile.toString(),
                        x
                    )
                    CoreCmd.moveResult(x, path)
                } else {
                    CoreUtil.tarExtractUsingRoot(exFile.toString(), path)
                }
                exFile!!.delete()
            } catch (e: Exception) {
                handler.post {
                    exFile!!.delete()
                    callback.onDownloadingFailed(e)
                }
            }
        }
    }
}