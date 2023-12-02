package xyz.chz.bfm.util.command

import xyz.chz.bfm.util.terminal.TerminalHelper.execRootCmd
import xyz.chz.bfm.util.terminal.TerminalHelper.execRootCmdSilent

object CoreCmd {
    private const val path = "/data/adb/box"

    val checkVerSing: String
        get() {
            val cmd = "$path/bin/sing-box version | awk '{print $3}' | awk '{print $1; exit}'"
            return execRootCmd(cmd)
        }

    val checkVerClashMeta: String
        get() {
            val cmd = "$path/bin/xclash/mihomo -v | awk '{print $3}' | awk '{print $1; exit}'"
            return execRootCmd(cmd)
        }

    fun moveResult(pathOut: String, pathDir: String) {
        val cmd = "mv -f $pathOut $pathDir && chmod 755 $pathDir && chown root:net_admin $pathDir"
        execRootCmdSilent(cmd)
    }
}