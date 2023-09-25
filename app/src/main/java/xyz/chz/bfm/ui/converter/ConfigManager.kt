package xyz.chz.bfm.ui.converter

import xyz.chz.bfm.ui.converter.config.ClashData
import xyz.chz.bfm.ui.converter.config.ConfigType
import xyz.chz.bfm.ui.converter.config.ConfigUtil
import java.util.regex.Pattern

object ConfigManager {

    fun importConfig(config: String, useIndent: Boolean): String {
        try {
            if (config.startsWith(ConfigType.VMESS.scheme)) {
                var result = config.replace(ConfigType.VMESS.scheme, "")
                result = ConfigUtil.decode(result)
                if (result.isEmpty()) {
                    return "failed decode vms"
                }
                return ClashData(result, useIndent).newVmessConfig()
            } else if (config.startsWith(ConfigType.VLESS.scheme)) {
                return ClashData(config, useIndent).newVlessConfig()
            } else if (config.startsWith(ConfigType.TROJAN.scheme) || config.startsWith(ConfigType.TROJANGO.scheme)) {
                return ClashData(config, useIndent).newTrojanConfig()
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
        return String.format(strRaw, config, proxyGroupBuilder(name, sb.toString()), name)
    }

    private fun proxyGroupBuilder(nameProxy: String, listProxy: String): String {
        val sb = StringBuilder()
        sb.appendLine("- name: $nameProxy")
        sb.appendLine("  type: select")
        sb.appendLine("  proxies:")
        sb.append(listProxy)
        return sb.toString()
    }

}