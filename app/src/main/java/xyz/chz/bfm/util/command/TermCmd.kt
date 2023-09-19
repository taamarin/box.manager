package xyz.chz.bfm.util.command

import xyz.chz.bfm.util.magisk.MagiskHelper.execRootCmd
import xyz.chz.bfm.util.magisk.MagiskHelper.execRootCmdSilent
import xyz.chz.bfm.util.magisk.MagiskHelper.execRootCmdVoid
import kotlin.concurrent.thread

object TermCmd {
    val path = "/data/adb/box/"

    fun isProxying(): Boolean {
        return execRootCmdSilent("if [ -f ${path}run/box.pid ] ; then exit 1;fi") == 1
    }

    fun renewBox(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "${path}scripts/box.iptables renew"
            execRootCmdVoid(cmd, callback)
        }
    }

    fun start(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "${path}scripts/box.service start && ${path}scripts/box.iptables enable"
            execRootCmdVoid(cmd, callback)
        }
    }

    fun stop(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "${path}scripts/box.iptables disable && ${path}scripts/box.service stop"
            execRootCmdVoid(cmd, callback)
        }
    }

    fun readLog(): String {
        return execRootCmd("cat ${path}run/runs.log")
    }

    fun linkDasboard(): String {
        return execRootCmd("grep 'external-controller:' ${path}clash/config.yaml | awk '{print $2}'")
    }

    val isBlackListMode: Boolean
        get() {
            val cmd =
                "grep 'proxy_mode' ${path}settings.ini | sed 's/^.*=//' | sed 's/\"//g' | awk '{print $1; exit}'"
            return "blacklist" == execRootCmd(cmd)
        }

    fun setWhitelistOrBlacklist(state: Boolean): Boolean {
        val s = if(state) "blacklist" else "whitelist"
        return execRootCmdSilent("sed -i 's/proxy_mode=.*/proxy_mode=\"$s\"/;' ${path}settings.ini") != -1
    }

    val appidList: HashSet<String>
        get() {
            val s = HashSet<String>()
            val cmd =
                "grep 'packages_list' ${path}settings.ini | sed 's/^.*=//' | sed 's/(//g' | sed 's/)//g' | awk 'END{print}'"
            val result = execRootCmd(cmd)
            if ("" == result) {
                return s
            }
            val appIds = result.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in appIds) {
                s.add(i)
            }
            return s
        }

    private fun setAppIdList(): Boolean {
        return execRootCmdSilent("sed -i 's/packages_list=.*/packages_list=()/;' ${path}settings.ini") != -1
    }

    fun setAppidList(s: HashSet<String?>): Boolean {
        if (s.size == 0) {
            return setAppIdList()
        }
        val cmd = StringBuilder("sed -i 's/packages_list=.*/packages_list=( ")
        for (i in s) {
            cmd.append(i).append(" ")
        }
        cmd.append(")/;' ${path}settings.ini")
        return execRootCmdSilent(cmd.toString().trim { it <= ' ' }) != -1
    }

}