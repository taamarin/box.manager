package xyz.chz.bfm.util.command

import xyz.chz.bfm.util.terminal.TerminalHelper.execRootCmd

object SettingCmd {

    val networkMode: String
        get() = execRootCmd("grep 'network_mode=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setNetworkMode(mode: String): String {
        return execRootCmd("sed -i 's/network_mode=.*/network_mode=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val proxyMode: String
        get() = execRootCmd("sed -n 's/^mode:\\([^ ]*\\).*/\\1/p' /data/adb/box/package.list.cfg")

    fun setProxyMode(mode: String): String {
        return execRootCmd("sed -i 's/^mode:[^ ]*/mode:$mode/' /data/adb/box/package.list.cfg")
    }

    val cron: String
        get() = execRootCmd("grep 'run_crontab=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setCron(mode: String): String {
        return execRootCmd("sed -i 's/run_crontab=.*/run_crontab=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val geo: String
        get() = execRootCmd("grep 'update_geo=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setGeo(mode: String): String {
        return execRootCmd("sed -i 's/update_geo=.*/update_geo=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val memcg: String
        get() = execRootCmd("grep 'cgroup_memcg=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setMemcg(mode: String): String {
        return execRootCmd("sed -i 's/cgroup_memcg=.*/cgroup_memcg=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val blkio: String
        get() = execRootCmd("grep 'cgroup_blkio=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setBlkio(mode: String): String {
        return execRootCmd("sed -i 's/cgroup_blkio=.*/cgroup_blkio=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val cpuset: String
        get() = execRootCmd("grep 'cgroup_cpuset=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setCpuset(mode: String): String {
        return execRootCmd("sed -i 's/cgroup_cpuset=.*/cgroup_cpuset=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val subs: String
        get() = execRootCmd("grep 'update_subscription=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setSubs(mode: String): String {
        return execRootCmd("sed -i 's/update_subscription=.*/update_subscription=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val redirHost: Boolean
        get() = "redir-host" == execRootCmd("grep 'enhanced-mode:' /data/adb/box/clash/config.yaml | awk '{print $2}'")

    fun setRedirHost(mode: String): String {
        return execRootCmd("sed -i 's/enhanced-mode:.*/enhanced-mode: $mode/;' /data/adb/box/clash/config.yaml")
    }

    val quic: Boolean
        get() = "enable" == execRootCmd("grep 'quic=' /data/adb/box/scripts/box.iptables | sed 's/^.*=//' | sed 's/\"//g'")

    fun setQuic(mode: String): String {
        return execRootCmd("sed -i 's/quic=.*/quic=\"$mode\"/;' /data/adb/box/scripts/box.iptables")
    }

    val unified: Boolean
        get() = "true" == execRootCmd("grep 'unified-delay:' /data/adb/box/clash/config.yaml | awk '{print $2}'")

    fun setUnified(mode: String): String {
        return execRootCmd("sed -i 's/unified-delay:.*/unified-delay: $mode/;' /data/adb/box/clash/config.yaml")
    }

    val geodata: Boolean
        get() = "true" == execRootCmd("grep 'geodata-mode:' /data/adb/box/clash/config.yaml | awk '{print $2}'")

    fun setGeodata(mode: String): String {
        return execRootCmd("sed -i 's/geodata-mode:.*/geodata-mode: $mode/;' /data/adb/box/clash/config.yaml")
    }

    val tcpCon: Boolean
        get() = "true" == execRootCmd("grep 'tcp-concurrent:' /data/adb/box/clash/config.yaml | awk '{print $2}'")

    fun setTcpCon(mode: String): String {
        return execRootCmd("sed -i 's/tcp-concurrent:.*/tcp-concurrent: $mode/;' /data/adb/box/clash/config.yaml")
    }

    val sniffer: Boolean
        get() = "true" == execRootCmd("grep -C 1 'sniffer:' /data/adb/box/clash/config.yaml  | grep 'enable:' | awk '{print $2}'")

    fun setSniffer(mode: String): String {
        return execRootCmd("sed -i '/^sniffer:/{n;s/enable:.*/enable: $mode/;}' /data/adb/box/clash/config.yaml")
    }

    val ipv6: Boolean
        get() = "true" == execRootCmd("grep 'ipv6=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setIpv6(mode: String): String {
        return execRootCmd("sed -i 's/ipv6=.*/ipv6=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val findProc: String
        get() = execRootCmd("grep 'find-process-mode:' /data/adb/box/clash/config.yaml | awk '{print $2}'")

    fun setFindProc(mode: String): String {
        return execRootCmd("sed -i 's/find-process-mode:.*/find-process-mode: $mode/;' /data/adb/box/clash/config.yaml")
    }

    val findConf: String
        get() = execRootCmd("grep 'name_clash_config=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setFindConf(mode: String): String {
        return execRootCmd("sed -i 's/name_clash_config=.*/name_clash_config=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val clashType: String
        get() = execRootCmd("grep 'xclash_option=' /data/adb/box/settings.ini | sed 's/^.*=//' | sed 's/\"//g'")

    fun setClashType(mode: String): String {
        return execRootCmd("sed -i 's/xclash_option=.*/xclash_option=\"$mode\"/;' /data/adb/box/settings.ini")
    }

    val core: String
        get() = execRootCmd("grep 'bin_name=' /data/adb/box/settings.ini | sed 's/^.*=//g' | sed 's/\"//g'")

//    fun setCore(x: String): Boolean {
//        return execRootCmdSilent("sed -i 's/bin_name=.*/bin_name=$x/;' /data/adb/box/settings.ini") != -1
//    }

    var setCore: String = ""
        set(value) {
            field = value
            execRootCmd("sed -i 's/bin_name=.*/bin_name=$field/;' /data/adb/box/settings.ini")
        }

}