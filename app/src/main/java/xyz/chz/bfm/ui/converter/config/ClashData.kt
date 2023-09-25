package xyz.chz.bfm.ui.converter.config

import android.net.Uri
import org.json.JSONObject
import java.net.URLDecoder
import kotlin.random.Random

class ClashData(private val masuk: String, private val indent: Boolean) {

    fun newVmessConfig(): String {
        val jo = JSONObject(masuk)
        val sb = StringBuilder()
        val idnt: String = if (indent) "  " else ""
        sb.appendLine(
            "${idnt}- name: ${
                jo.optString(
                    "ps",
                    jo.optString("add", "new")
                )
            }_vmess_${Random.nextInt(0, 7000)}"
        )
        sb.appendLine("$idnt  server: ${jo.optString("add", jo.optString("host", ""))}")
        sb.appendLine("$idnt  port: ${jo.getString("port")}")
        sb.appendLine("$idnt  type: vmess")
        sb.appendLine("$idnt  uuid: ${jo.getString("id")}")
        sb.appendLine("$idnt  alterId: ${jo.optString("aid", "0")}")
        sb.appendLine("$idnt  cipher: ${jo.optString("scy", "auto")}")
        sb.appendLine("$idnt  tls: ${jo.optString("tls", "") == "tls"}")
        sb.appendLine("$idnt  skip-cert-verify: true")
        sb.appendLine("$idnt  udp: true")
        when (jo.optString("net", "tcp")) {
            "ws" -> {
                sb.appendLine("$idnt  network: ws")
                sb.appendLine("$idnt  ws-opts:")
                sb.appendLine("$idnt    path: ${jo.optString("path", "/")}")
                sb.appendLine("$idnt    headers:")
                sb.append("$idnt      Host: ${jo.getString("host")}")
            }

            "grpc" -> {
                sb.appendLine("$idnt  network: grpc")
                sb.appendLine("$idnt  grpc-opts:")
                sb.append("$idnt    grpc-service-name: ${jo.getString("host")}")
            }

            "h2" -> {
                sb.appendLine("$idnt  network: h2")
                sb.appendLine("$idnt  h2-opts:")
                sb.append("$idnt    path: ${jo.optString("path", "/")}")
            }

            "tcp" -> {
                if (jo["type"] == "http") {
                    sb.appendLine("$idnt  network: http")
                    sb.appendLine("$idnt  http-opts:")
                    sb.append("$idnt    path: ${jo.optString("path", "/")}")
                }
            }

            else -> throw Exception("${jo.getString("net")} not supported")
        }
        return sb.toString()
    }

    fun newVlessConfig(): String {
        val sb = StringBuilder()
        val idnt: String = if (indent) "  " else ""
        var url = masuk
        if (!url.contains("@")) url = ConfigUtil.safeDecodeURLB64(url)
        val uri = Uri.parse(url)
        sb.appendLine(
            "${idnt}- name: ${uri.fragment ?: "new"}_${uri.scheme}_${
                Random.nextInt(
                    0,
                    7000
                )
            }"
        )
        sb.appendLine("$idnt  server: ${uri.host ?: ""}")
        sb.appendLine("$idnt  port: ${uri.port}")
        sb.appendLine("$idnt  type: vless")
        if (uri.userInfo == null || uri.userInfo!!.isEmpty()) throw Exception("no user info")
        sb.appendLine("$idnt  uuid: ${uri.userInfo}")
        sb.appendLine(
            "$idnt  tls: ${
                (ConfigUtil.getQueryParams(
                    uri,
                    "security"
                ) ?: "") == "tls"
            }"
        )
        sb.appendLine(
            "$idnt  servername: ${
                ConfigUtil.getQueryParams(
                    uri,
                    "sni"
                ) ?: ConfigUtil.getQueryParams(uri, "host") ?: uri.host!!
            }"
        )
        sb.appendLine("$idnt  skip-cert-verify: true")
        sb.appendLine("$idnt  udp: true")
        if (ConfigUtil.getQueryParams(
                uri,
                "flow"
            ) != null
        ) sb.appendLine("$idnt  flow: ${ConfigUtil.getQueryParams(uri, "flow")!!}")
        val type = ConfigUtil.getQueryParams(uri, "type") ?: "tcp"
        val decodePath =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "path") ?: "", "UTF-8")
        val decodeHost =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "host") ?: "", "UTF-8")
        when (type) {
            "ws" -> {
                sb.appendLine("$idnt  network: ws")
                sb.appendLine("$idnt  ws-opts:")

                sb.appendLine("$idnt    path: $decodePath")
                sb.appendLine("$idnt    headers:")
                sb.append("${idnt}      Host: $decodeHost")
            }

            "tcp" -> {}
            "http" -> {}
            "grpc" -> {
                sb.appendLine("$idnt  network: grpc")
                sb.appendLine("$idnt  grpc-opts:")
                sb.append(
                    "$idnt    grpc-service-name: ${
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

    fun newTrojanConfig(): String {
        val sb = StringBuilder()
        val idnt: String = if (indent) "  " else ""
        var url = masuk
        if (!url.contains("@")) url = ConfigUtil.safeDecodeURLB64(url)
        val uri = Uri.parse(url)
        sb.appendLine(
            "${idnt}- name: ${uri.fragment ?: "new"}_${uri.scheme}_${
                Random.nextInt(
                    0,
                    7000
                )
            }"
        )
        sb.appendLine("$idnt  server: ${uri.host ?: ""}")
        sb.appendLine("$idnt  port: ${uri.port}")
        sb.appendLine("$idnt  type: trojan")
        if (uri.userInfo == null || uri.userInfo!!.isEmpty()) throw Exception("no user info")
        sb.appendLine("$idnt  password: ${uri.userInfo}")
        sb.appendLine(
            "$idnt  sni: ${
                ConfigUtil.getQueryParams(
                    uri,
                    "sni"
                ) ?: ConfigUtil.getQueryParams(uri, "host") ?: uri.host!!
            }"
        )
        sb.appendLine("$idnt  skip-cert-verify: true")
        sb.appendLine("$idnt  udp: true")
        if (ConfigUtil.getQueryParams(
                uri,
                "flow"
            ) != null
        ) sb.appendLine("$idnt  flow: ${ConfigUtil.getQueryParams(uri, "flow")!!}")
        val alpnStr = URLDecoder.decode(ConfigUtil.getQueryParams(uri, "alpn") ?: "", "UTF-8")
        if (alpnStr != "") sb.appendLine("$idnt  alpn: ${alpnStr.split(",")}")

        val type = ConfigUtil.getQueryParams(uri, "type") ?: "tcp"
        val decodePath =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "path") ?: "", "UTF-8")
        val decodeHost =
            URLDecoder.decode(ConfigUtil.getQueryParams(uri, "host") ?: "", "UTF-8")

        when (type) {
            "ws" -> {
                sb.appendLine("$idnt  network: ws")
                sb.appendLine("$idnt  ws-opts:")

                sb.appendLine("$idnt    path: $decodePath")
                sb.appendLine("$idnt    headers:")
                sb.append("$idnt      Host: $decodeHost")
            }

            "tcp" -> {}
            "http" -> {}
            "grpc" -> {
                sb.appendLine("$idnt  network: grpc")
                sb.appendLine("$idnt  grpc-opts:")
                sb.append(
                    "$idnt    grpc-service-name: ${
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
