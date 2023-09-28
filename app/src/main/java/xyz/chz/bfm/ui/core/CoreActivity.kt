package xyz.chz.bfm.ui.core

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.ActivityCoreBinding
import xyz.chz.bfm.util.META_REPO
import xyz.chz.bfm.util.OkHttpHelper
import java.io.IOException

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkMeta()
    }

    private fun checkMeta() = with(binding) {
        tvClash.text = "Checking for Clash Meta"
        prgClash.isVisible = true
        OkHttpHelper().reqGithub(META_REPO, object: Callback {
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

    private fun parseClash(str: String) = with(binding) {
        try {
            val jo = JSONObject(str)
            val arrAsset = jo.getJSONArray("assets")
            val objectLatest: String = arrAsset.getJSONObject(0).toString(2)
            tvResTest.text = objectLatest.replace("\\", "")
        }catch (e: JSONException){
            tvResTest.text= e.message!!.toString()
        }
    }
}