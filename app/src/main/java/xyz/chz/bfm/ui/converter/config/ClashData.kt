package xyz.chz.bfm.ui.converter.config

import org.json.JSONObject

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

            else -> throw Exception("${jo.getString("net")} Not Supported")
        }

        sb.appendLine("udp: true")
        return sb.toString()
    }

}
