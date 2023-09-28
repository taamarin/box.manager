package xyz.chz.bfm.ui.converter

import org.json.JSONObject
import xyz.chz.bfm.ui.converter.config.ClashData
import xyz.chz.bfm.ui.converter.config.ConfigType
import xyz.chz.bfm.ui.converter.config.ConfigUtil
import xyz.chz.bfm.ui.converter.config.SingBoxData
import java.util.regex.Pattern

object ConfigManager {

    fun importConfig(config: String, isClash: Boolean, useIndent: Boolean = false): String {
        try {
            if (config.startsWith(ConfigType.VMESS.scheme)) {
                var result = config.replace(ConfigType.VMESS.scheme, "")
                result = ConfigUtil.decode(result)
                if (result.isEmpty()) {
                    return "failed decode vms"
                }
                return if (isClash) ClashData(result, useIndent).newVmessConfig() else SingBoxData(
                    result
                ).vmessSing()
            } else if (config.startsWith(ConfigType.VLESS.scheme)) {
                return if (isClash) ClashData(config, useIndent).newVlessConfig() else SingBoxData(
                    config
                ).vlessSing()
            } else if (config.startsWith(ConfigType.TROJAN.scheme) || config.startsWith(ConfigType.TROJANGO.scheme)) {
                return if (isClash) ClashData(config, useIndent).newTrojanConfig() else SingBoxData(
                    config
                ).trojanSing()
            } else {
                return ""
            }
        } catch (_: Exception) {
        }
        return ""
    }

    fun fullClashSimple(config: String, strRaw: String): String {
        val name = "BOX-FOR-ROOT"
        val p = Pattern.compile("- name:(.*)")
        val m = p.matcher(config)
        val sb = StringBuilder()
        while (m.find()) {
            sb.appendLine("    - ${m.group(1)?.trim()}")
        }
        return String.format(
            strRaw,
            config,
            ClashData().proxyGroupBuilder(name, sb.toString()),
            name
        )
    }

    fun fullSingSimple(config: String, strRaw: String): String {
        val s = ArrayList<String>()
        val p = Pattern.compile("\"tag\":(.*)")
        val m = p.matcher(config)
        val sb = StringBuilder()
        while (m.find()) {
            s.add(m.group(1)?.trim()!!.replace("\"|,", ""))
        }
        sb.appendLine(SingBoxData().buildHeaderSlector(s))
        sb.appendLine(SingBoxData().buildHeaderBestUrl(s))
        sb.append(config)
        sb.appendLine(SingBoxData().buildFooter())
        val strResult = String.format(strRaw, sb.toString())
        return JSONObject(strResult).toString(2).replace("\\", "").replace("null,", "")
    }
}