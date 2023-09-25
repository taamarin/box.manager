package xyz.chz.bfm.ui.converter.config

enum class ConfigType(val scheme: String) {
    VMESS("vmess://"),
    VLESS("vless://"),
    TROJAN("trojan://"),
    TROJANGO("trojan-go://")
}