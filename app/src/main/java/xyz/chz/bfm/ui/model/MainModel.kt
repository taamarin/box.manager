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
import xyz.chz.bfm.util.command.TermUtil
import javax.inject.Inject

@HiltViewModel
class MainModel @Inject constructor() : ViewModel() {

    private val _log = MutableLiveData<String>()
    val log: LiveData<String> get() = _log

    fun data(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                _log.postValue(TermUtil.readLog())
                delay(1000)
            }
        }
    }
}