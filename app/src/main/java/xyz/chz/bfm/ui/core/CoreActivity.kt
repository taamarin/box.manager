package xyz.chz.bfm.ui.core

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import xyz.chz.bfm.databinding.ActivityCoreBinding
import xyz.chz.bfm.ui.core.util.CoreUtil
import xyz.chz.bfm.ui.core.util.DownloaderCore
import xyz.chz.bfm.ui.core.util.IDownloadCore
import xyz.chz.bfm.util.META_DOWNLOAD
import xyz.chz.bfm.util.META_REPO
import xyz.chz.bfm.util.OkHttpHelper
import xyz.chz.bfm.util.SING_DOWNLOAD
import xyz.chz.bfm.util.SING_REPO
import xyz.chz.bfm.util.command.CoreCmd
import xyz.chz.bfm.util.toast
import xyz.chz.bfm.util.urlText
import java.io.IOException

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class CoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoreBinding
    private lateinit var dCore: DownloaderCore

    private var urlClash = ""
    private var urlSing = ""
    private var tagNameClash = ""
    private var tagNameSing = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dCore = DownloaderCore(this)
        checkClash()
        buttonUp()
    }

    private fun checkClash() = with(binding) {
        tvClash.text = "Checking for mihomo"
        prgClash.isVisible = true
        OkHttpHelper().reqGithub(META_REPO, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvClash.text = "Failed to get repo clash, check ur internet connection"
                    prgClash.isVisible = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respom = response.body!!.string()
                runOnUiThread {
                    urlClash = parseClash(respom)
                    if (parseVersionClashMeta() != CoreCmd.checkVerClashMeta) {
                        tvClash.text = "Update available mihomo"
                        btnClash.isVisible = true
                    } else {
                        tvClash.text = "mihomo has Latest Version"
                        imgDoneClash.isVisible = true
                        btnClash.isVisible = false
                    }
                    prgClash.isVisible = false
                }
            }
        })
    }

    private fun parseClash(str: String): String {
        return try {
            val jo = JSONObject(str)
            tagNameClash = jo.getString("tag_name")
            if (CoreUtil.getAbis().contains("arm64")) {
                "$META_DOWNLOAD/$tagNameClash/mihomo-android-arm64-v8-$tagNameClash.gz"
            } else {
                "$META_DOWNLOAD/$tagNameClash/mihomo-android-armv7-$tagNameClash.gz"
            }
        } catch (e: JSONException) {
            e.message!!.toString()
        }
    }

    private fun parseVersionClashMeta(): String {
        return try {
            "$META_DOWNLOAD/$tagNameClash/version.txt".urlText()
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun buttonUp() = with(binding) {
        btnClash.apply {
            setOnClickListener {
                dCore.downloadFile(
                    urlClash,
                    "clash_meta.gz",
                    "/data/adb/box/bin/xclash/mihomo",
                    object : IDownloadCore {
                        override fun onDownloadingStart() {
                            prgHrzClash.isVisible = true
                            btnClash.isVisible = false
                        }

                        override fun onDownloadingProgress(progress: Int) {
                            prgHrzClash.progress = progress
                        }

                        override fun onDownloadingComplete() {
                            btnClash.isVisible = false
                            prgHrzClash.isVisible = false
                            imgDoneClash.isVisible = true
                            tvClash.text = "mihomo has Latest Version"
                        }

                        override fun onDownloadingFailed(e: Exception?) {
                            toast("failed downloading clash", this@CoreActivity)
                            btnClash.isVisible = true
                            prgHrzClash.progress = 0
                            prgHrzClash.isVisible = false
                        }
                    })
                }
        }
    }
}
