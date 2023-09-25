package xyz.chz.bfm.ui.converter.config

import android.net.Uri
import org.json.JSONObject
import java.net.URLDecoder
import kotlin.random.Random

class ClashData(private val masuk: String) {

    fun newVmessConfig(): String {
        val jo = JSONObject(masuk)
        val sb = StringBuilder()
        sb.appendLine("name: ${jo.optString("ps", jo.optString("add", "new"))}")
        sb.appendLine("server: ${jo.optString("add", jo.optString("host", ""))}")
        sb.appendLine("port: ${jo.getString("port")}")
        sb.appendLine("type: vmess")
        sb.appendLine("uuid: ${jo.getString("id")}")
        sb.appendLine("alterId: ${jo.optString("aid", "0")}")
        sb.appendLine("cipher: ${jo.optString("scy", "auto")}")
        sb.appendLine("tls: ${jo.optString("tls", "") == "tls"}")
        sb.appendLine("skip-cert-verify: true")
        sb.appendLine("udp: true")
        when (jo.optString("net", "tcp")) {
            "ws" -> {
                sb.appendLine("network: ws")
                sb.appendLine("ws-opts:")
                sb.appendLine("  path: ${jo.optString("path", "/")}")
                sb.appendLine("  headers:")
                sb.appendLine("    Host: ${jo.getString("host")}")
            }

            "grpc" -> {
                sb.appendLine("network: grpc")
                sb.appendLine("grpc-opts:")
                sb.appendLine("  grpc-service-name: ${jo.getString("host")}")
            }

            "h2" -> {
                sb.appendLine("network: h2")
                sb.appendLine("h2-opts:")
                sb.appendLine("  path: ${jo.optString("path", "/")}")
            }

            "tcp" -> {
                if (jo["type"] == "http") {
                    sb.appendLine("network: http")
                    sb.appendLine("http-opts:")
                    sb.appendLine("  path: ${jo.optString("path", "/")}")
                }
            }

            else -> throw Exception("${jo.getString("net")} not supported")
        }
        return sb.toString()
    }

    fun newVlessConfig(): String {
        val sb = StringBuilder()
        var url = masuk
        if (!url.contains("@")) url = ConfigUtil.safeDecodeURLB64(url)
        val uri = Uri.parse(url)
        sb.appendLine("name: ${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 7000)}")
        sb.appendLine("type: vless")
        if (uri.userInfo == null || uri.userInfo!!.isEmpty()) throw Exception("no user info")
        sb.appendLine("uuid: ${uri.userInfo}")
        sb.appendLine("server: ${uri.host ?: ""}")
        sb.appendLine("port: ${uri.port}")
        sb.appendLine(
            "tls: ${
                (ConfigUtil.getQueryParams(
                    uri,
                    "security"
                ) ?: "") == "tls"
            }"
        )
        sb.appendLine(
            "servername: ${
                ConfigUtil.getQueryParams(
                    uri,
                    "sni"
                ) ?: ConfigUtil.getQueryParams(uri, "host") ?: uri.host!!
            }"
        )
        sb.appendLine("skip-cert-verify: true")
        sb.appendLine("udp true")
        if (ConfigUtil.getQueryParams(
                uri,
                "flow"
            ) != null
        ) sb.appendLine("flow: ${ConfigUtil.getQueryParams(uri, "flow")!!}")
        val type = ConfigUtil.getQueryParams(uri, "type") ?: "tcp"
        val decodePath =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "path") ?: "", "UTF-8")
        val decodeHost =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "host") ?: "", "UTF-8")
        when (type) {
            "ws" -> {
                sb.appendLine("network: ws")
                sb.appendLine("ws-opts:")

                sb.appendLine("  path: $decodePath")
                sb.appendLine("  headers:")
                sb.appendLine("    Host: $decodeHost")
            }

            "tcp" -> {}
            "http" -> {}
            "grpc" -> {
                sb.appendLine("network: grpc")
                sb.appendLine("grpc-opts:")
                sb.appendLine(
                    "  grpc-service-name: ${
                        ConfigUtil.getQueryParams(
                            uri,
                            "serviceName"
                        ) ?: ""
                    }"
                )
            }

            else -> throw Exception("$type not supported")
        }
        return sb.toString()
    }

}
