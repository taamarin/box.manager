package xyz.chz.bfm.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import xyz.chz.bfm.util.OkHttpHelper
import xyz.chz.bfm.util.command.TermCmd
import xyz.chz.bfm.util.myReplacer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _log = MutableLiveData<String>()
    val log: LiveData<String> get() = _log

    private val _theresult = MutableLiveData<String?>()
    val theresult: LiveData<String?> = _theresult

    fun dataLog(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                _log.postValue(
                    TermCmd.readLog().myReplacer(
                        "\\[Info\\]".toRegex() to "<font color=\"#58869e\">[Info] </font>",
                        "\\[Debug\\]".toRegex() to "<font color=\"#156238\">[Debug] </font>",
                        "\\[Error\\]".toRegex() to "<font color=\"#8e2e41\">[Error] </font>",
                        "\\[Warning\\]".toRegex() to "<font color=\"#fe9a01\">[Warning] </font>",
                        "\n".toRegex() to "<br>"
                    )
                )
                delay(1500)
            }
        }
    }

    fun getTextFromUrl(url: String) {
        OkHttpHelper().getRawTextFromURL(url, object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                _theresult.value = e.message!!
            }

            override fun onResponse(call: Call, response: Response) {
                _theresult.postValue(response.body!!.string())
            }
        })
    }
}