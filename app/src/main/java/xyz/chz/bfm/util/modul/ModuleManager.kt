package xyz.chz.bfm.util.modul

import xyz.chz.bfm.util.terminal.TerminalHelper

object ModuleManager {
    val moduleVersionCode: String
        get() {
            val cmd = "cat /data/adb/modules/box_for_root/module.prop | grep '^versionCode='"
            val result = TerminalHelper.execRootCmd(cmd)
            return result.substringAfter("=", "").trim()
        }

    val moduleVersion: String
        get() {
            val cmd = "cat /data/adb/modules/box_for_root/module.prop | grep '^version='"
            val result = TerminalHelper.execRootCmd(cmd)
            return result.substringAfter("=", "").trim()
        }

}