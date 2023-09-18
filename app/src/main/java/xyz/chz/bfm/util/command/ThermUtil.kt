package xyz.chz.bfm.util.command

import xyz.chz.bfm.util.magisk.MagiskHelper
import kotlin.concurrent.thread

object ThermUtil {
    fun isProxying(): Boolean {
        return MagiskHelper.execRootCmdSilent("if [ -f /data/adb/box/run/box.pid ] ; then exit 1;fi") == 1
    }

    fun start(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "/data/adb/box/scripts/box.service start && /data/adb/box/scripts/box.iptables enable"
            MagiskHelper.execRootCmdVoid(cmd, callback)
        }
    }

    fun stop(callback: (Boolean) -> Unit) {
        thread {
            val cmd = "/data/adb/box/scripts/box.iptables disable && /data/adb/box/scripts/box.service stop"
            MagiskHelper.execRootCmdVoid(cmd, callback)
        }
    }
}