package xyz.chz.bfm.ui.converter.config

import org.json.JSONObject
import xyz.chz.bfm.util.Util
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
            "global_padding" to false
            "authenticated_length" to true
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
                        "headers" to {
                            "Host" to jo.optString("host", jo.optString("add", ""))
                        }
                        "max_early_data" to 0
                        "early_data_header_name" to "Sec-WebSocket-Protocol"
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
            "domain_strategy" to "ipv4_only"
        }

        return json.toString(2)
    }

    fun buildHeaderSlector(arrName: ArrayList<String>): String {
        val sb = StringBuilder()
        sb.append("{\"type\": \"selector\",\"tag\": \"Internet\",")
        sb.append("\"outbounds\": [\"Best Latency\",")
        for (x in arrName)
            sb.append(x)
        sb.append("]},")
        return sb.toString().replace(",]", "]")
    }

    fun buildHeaderBestUrl(arrName: ArrayList<String>): String {
        val sb = StringBuilder()
        sb.append("{\"type\": \"urltest\",\"tag\": \"Best Latency\",")
        sb.append("\"outbounds\": [")
        for (x in arrName)
            sb.append(x)
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