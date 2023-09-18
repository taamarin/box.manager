package xyz.chz.bfm.util.command

import xyz.chz.bfm.util.magisk.MagiskHelper
import kotlin.concurrent.thread

object ThermUtil {
    val path = "/data/adb/box/"

    fun isProxying(): Boolean {
        return MagiskHelper.execRootCmdSilent("if [ -f ${path}run/box.pid ] ; then exit 1;fi") == 1
    }

    fun start(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "${path}scripts/box.service start && ${path}scripts/box.iptables enable"
            MagiskHelper.execRootCmdVoid(cmd, callback)
        }
    }

    fun stop(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "${path}scripts/box.iptables disable && ${path}scripts/box.service stop"
            MagiskHelper.execRootCmdVoid(cmd, callback)
        }
    }

    fun readLog(): String {
        return MagiskHelper.execRootCmd("cat ${path}run/runs.log")
    }

    fun linkDasboard(): String {
        return MagiskHelper.execRootCmd("grep 'external-controller:' ${path}clash/config.yaml | awk '{print $2}'")
    }
}