package xyz.chz.bfm.ui.converter.config

import android.net.Uri
import org.json.JSONObject
import xyz.chz.bfm.util.Util
import java.net.URLDecoder
import kotlin.random.Random

//Template sing-box copied from singribet.deno.dev
class SingBoxData(private val masuk: String = "") {

    fun vmessSing(): String {
        val jo = JSONObject(masuk)
        val json = Util.json {
            "tag" to "${jo.optString("ps", jo.optString("add", "new"))}_vmess_${
                Random.nextInt(
                    0,
                    7000
                )
            }"
            "type" to "vmess"
            "server" to jo.optString("add", jo.optString("host", ""))
            "server_port" to jo.getString("port").toInt()
            "uuid" to jo.getString("id")
            "security" to jo.optString("scy", "auto")
            "alter_id" to jo.optString("aid", "0").toInt()
            "multiplex" to {
                "enabled" to false
                "protocol" to "smux"
                "max_streams" to 32
            }
            if (jo.optString("tls", "") == "tls") {
                "tls" to {
                    "enabled" to true
                    "server_name" to jo.optString("sni", jo.optString("add", ""))
                    "insecure" to true
                    "disable_sni" to false
                }
            }
            val type = jo.optString("net", "tcp")
            when (type) {
                "ws" -> {
                    "transport" to {
                        "type" to "ws"
                        "path" to jo.optString("path", "/")
                        "host" to jo.optString("host", jo.optString("add", ""))
                        "headers" to {
                            "Host" to jo.optString("host", jo.optString("add", ""))
                        }
                    }
                }

                "grpc" -> {
                    "transport" to {
                        "type" to "grpc"
                        "service_name" to jo.optString("path", "/")
                        "idle_timeout" to "15s"
                        "ping_timeout" to "15s"
                        "permit_without_stream" to false
                    }
                }

                "tcp" -> {}
                "h2" -> {}
                else -> throw Exception("$type not supported")
            }
        }

        return json.toString(2)
    }

    fun vlessSing(): String {
        var url = masuk
        if (!url.contains("@")) url = ConfigUtil.safeDecodeURLB64(url)
        val uri = Uri.parse(url)
        val json = Util.json {
            "tag" to "${uri.fragment ?: "new"}_${uri.scheme}_${
                Random.nextInt(
                    0,
                    7000
                )
            }"
            "type" to "vless"
            ("server" to uri.host)
            "server_port" to uri.port.toInt()
            if (uri.userInfo == null || uri.userInfo!!.isEmpty()) throw Exception("no user info")
            "uuid" to uri.userInfo
            if (ConfigUtil.getQueryParams(
                    uri,
                    "flow"
                ) != null
            ) "flow" to ConfigUtil.getQueryParams(uri, "flow")!! else "flow" to ""
            "packet_encoding" to "xudp"
            "multiplex" to {
                "enabled" to false
                "protocol" to "smux"
                "max_streams" to 32
            }
            if ((ConfigUtil.getQueryParams(uri, "security") ?: "") == "tls") {
                "tls" to {
                    "enabled" to true
                    "server_name" to (ConfigUtil.getQueryParams(uri, "sni")
                        ?: ConfigUtil.getQueryParams(uri, "host") ?: uri.host!!)
                    "insecure" to true
                    "disable_sni" to false
                }
            }
            val decodePath =
                URLDecoder.decode(ConfigUtil.getQueryParams(uri, "path") ?: "", "UTF-8")
            val decodeHost =
                URLDecoder.decode(ConfigUtil.getQueryParams(uri, "host") ?: "", "UTF-8")
            val type = ConfigUtil.getQueryParams(uri, "type") ?: "tcp"
            when (type) {
                "ws" -> {
                    "transport" to {
                        "type" to "ws"
                        "path" to decodePath
                        "host" to decodeHost
                        "headers" to {
                            "Host" to decodeHost
                        }
                    }
                }

                "grpc" -> {
                    "transport" to {
                        "type" to "grpc"
                        ("service_name" to ConfigUtil.getQueryParams(uri, "serviceName"))
                        "idle_timeout" to "15s"
                        "ping_timeout" to "15s"
                        "permit_without_stream" to false
                    }
                }

                "tcp" -> {}
                "http" -> {}
                else -> throw Exception("$type not supported")
            }
        }
        return json.toString(2)
    }

    fun trojanSing(): String {
        var url = masuk
        if (!url.contains("@")) url = ConfigUtil.safeDecodeURLB64(url)
        val uri = Uri.parse(url)
        val json = Util.json {
            "tag" to "${uri.fragment ?: "new"}_${uri.scheme}_${
                Random.nextInt(
                    0,
                    7000
                )
            }"
            "type" to "trojan"
            ("server" to uri.host)
            "server_port" to uri.port.toInt()
            if (uri.userInfo == null || uri.userInfo!!.isEmpty()) throw Exception("no user info")
            "password" to uri.userInfo
            "multiplex" to {
                "enabled" to false
                "protocol" to "smux"
                "max_streams" to 32
            }
            if ((ConfigUtil.getQueryParams(uri, "security") ?: "") == "tls") {
                "tls" to {
                    "enabled" to true
                    "server_name" to (ConfigUtil.getQueryParams(uri, "sni")
                        ?: ConfigUtil.getQueryParams(uri, "host") ?: uri.host!!)
                    "insecure" to true
                    "disable_sni" to false
                }
            }
            val decodePath =
                URLDecoder.decode(ConfigUtil.getQueryParams(uri, "path") ?: "", "UTF-8")
            val decodeHost =
                URLDecoder.decode(ConfigUtil.getQueryParams(uri, "host") ?: "", "UTF-8")
            val type = ConfigUtil.getQueryParams(uri, "type") ?: "tcp"
            when (type) {
                "ws" -> {
                    "transport" to {
                        "type" to "ws"
                        "path" to decodePath
                        "host" to decodeHost
                        "headers" to {
                            "Host" to decodeHost
                        }
                    }
                }

                "grpc" -> {
                    "transport" to {
                        "type" to "grpc"
                        ("service_name" to ConfigUtil.getQueryParams(uri, "serviceName"))
                        "idle_timeout" to "15s"
                        "ping_timeout" to "15s"
                        "permit_without_stream" to false
                    }
                }

                "tcp" -> {}
                "http" -> {}
                else -> throw Exception("$type not supported")
            }
        }
        return json.toString(2)
    }

    fun buildHeaderSlector(arrName: ArrayList<String>): String {
        val sb = StringBuilder()
        sb.append("{\"type\": \"selector\",\"tag\": \"match\",")
        sb.append("\"outbounds\": [\"urltest\",")
        for (x in arrName)
            sb.append(x)
        sb.append("]},")
        return sb.toString().replace(",]", "]")
    }

    fun buildHeaderBestUrl(arrName: ArrayList<String>): String {
        val sb = StringBuilder()
        sb.append("{\"type\": \"urltest\",\"tag\": \"urltest\",")
        sb.append("\"outbounds\": [")
        for (x in arrName)
            sb.append(x)
        sb.append("]},")
        sb.append("{\"type\": \"selector\",\"tag\": \"ads-all\",")
        sb.append("\"outbounds\": [\"direct\",\"block\",\"match\"")
        sb.append("]},")
        return sb.toString().replace(",]", "]")
    }

    fun buildFooter(): String {
        val json = Util.json {
            "type" to "direct"
            "tag" to "direct"
        }
        val json2 = Util.json {
            "type" to "block"
            "tag" to "block"
        }
        val json3 = Util.json {
            "type" to "dns"
            "tag" to "dns-out"
        }
        return "${json.toString(2)},\n${json2.toString(2)},\n${json3.toString(2)}"
    }

}