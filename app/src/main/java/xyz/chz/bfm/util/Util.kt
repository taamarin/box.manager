package xyz.chz.bfm.util

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import xyz.chz.bfm.util.command.SettingCmd
import xyz.chz.bfm.util.command.TermCmd
import java.util.ArrayDeque
import java.util.Deque

object Util {
    var isProxyed = TermCmd.isProxying()
    private val handler = Handler(Looper.getMainLooper())

    fun runOnUiThread(action: () -> Unit) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action)
        } else {
            action.invoke()
        }
    }

    val isClashOrSing: Boolean
        get() {
            if (SettingCmd.core.contains("clash") or SettingCmd.core.contains("sing-box"))
                return true
            return false
        }

    fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
        return JsonObjectBuilder().json(build)
    }

    class JsonObjectBuilder {
        private val deque: Deque<JSONObject> = ArrayDeque()

        fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
            deque.push(JSONObject())
            this.build()
            return deque.pop()
        }

        infix fun <T> String.to(value: T) {
            val wrapped = when (value) {
                is Function0<*> -> json { value.invoke() }
                is Array<*> -> JSONArray().apply { value.forEach { put(it) } }
                else -> value
            }

            deque.peek().put(this, wrapped)
        }
    }
}