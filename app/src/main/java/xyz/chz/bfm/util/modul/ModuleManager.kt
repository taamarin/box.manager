package xyz.chz.bfm.util.modul

import xyz.chz.bfm.util.magisk.MagiskHelper

object ModuleManager {

    val moduleVersionCode: String
        get() {
            var cmd = "cat /data/adb/modules/box_for_root/module.prop | grep '^versionCode='"
            if (MagiskHelper.IS_MAGISK_LITE) {
                cmd = "cat /data/adb/lite_modules/box_for_root/module.prop | grep '^versionCode='"
            }
            val result = MagiskHelper.execRootCmd(cmd)
            return if ("" == result) "" else result.split("=".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        }

    val moduleVersion: String
        get() {
            var cmd = "cat /data/adb/modules/box_for_root/module.prop | grep '^version='"
            if (MagiskHelper.IS_MAGISK_LITE) {
                cmd = "cat /data/adb/lite_modules/box_for_root/module.prop | grep '^version='"
            }
            val result = MagiskHelper.execRootCmd(cmd)
            return if ("" == result) "" else result.split("=".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        }

}