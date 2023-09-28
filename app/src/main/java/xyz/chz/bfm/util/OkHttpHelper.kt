package xyz.chz.bfm.util

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpHelper {

    fun getRawTextFromURL(url: String, callback: Callback): Call {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun reqGithub(url: String, callback: Callback): Call {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }
}