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
import xyz.chz.bfm.databinding.ActivityCoreBinding
import xyz.chz.bfm.ui.core.util.DownloaderCore
import xyz.chz.bfm.ui.core.util.IDownloadCore
import xyz.chz.bfm.util.META_REPO
import xyz.chz.bfm.util.OkHttpHelper
import xyz.chz.bfm.util.SING_REPO
import java.io.IOException

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class CoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoreBinding
    private lateinit var dCore: DownloaderCore

    private var urlSing = ""
    private var urlClash = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dCore = DownloaderCore(this)
        checkClash()
        checkSing()
        buttonUp()
    }

    private fun checkClash() = with(binding) {
        tvClash.text = "Checking for Clash Meta"
        prgClash.isVisible = true
        OkHttpHelper().reqGithub(META_REPO, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvClash.text = "Failed to get repo, check ur internet connection"
                    prgClash.isVisible = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    parseClash(response.body!!.string())
                    prgClash.isVisible = false
                }
            }
        })
    }

    private fun checkSing() = with(binding) {
        tvSing.text = "Checking for SingBox"
        prgSing.isVisible = true
        OkHttpHelper().reqGithub(SING_REPO, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvSing.text = "Failed to get repo, check ur internet connection"
                    prgSing.isVisible = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    parseSing(response.body!!.string())
                    prgSing.isVisible = false
                }
            }
        })
    }

    private fun parseSing(str: String) = with(binding) {
        try {
            val ja = JSONArray(str)
            val jo = ja.getJSONObject(0)
            val arrAsset = jo.getJSONArray("assets")
            val objectLatest = arrAsset.getJSONObject(1)
            urlSing = objectLatest.getString("browser_download_url").replace("\\", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun parseClash(str: String) = with(binding) {
        try {
            val ja = JSONArray(str)
            val jo = ja.getJSONObject(0)
            val arrAsset = jo.getJSONArray("assets")
            val objectLatest = arrAsset.getJSONObject(0)
            urlClash = objectLatest.getString("browser_download_url").replace("\\", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun buttonUp() = with(binding) {
        btnClash.apply {
            setOnClickListener {
                dCore.downloadFile(urlClash, "clash.gz", object : IDownloadCore {
                    override fun onDownloadingStart() {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingProgress(progress: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingComplete() {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingFailed(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }

        btnSing.apply {
            setOnClickListener {
                dCore.downloadFile(urlSing, "sing.gz", object : IDownloadCore {
                    override fun onDownloadingStart() {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingProgress(progress: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingComplete() {
                        TODO("Not yet implemented")
                    }

                    override fun onDownloadingFailed(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
}
