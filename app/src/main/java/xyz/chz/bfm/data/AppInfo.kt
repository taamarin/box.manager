package xyz.chz.bfm.data

import android.content.pm.ApplicationInfo

data class AppInfo(
    val appName: String,
    val packageName: String,
    val applicationInfo: ApplicationInfo,
    val isSystemApp: Boolean,
    var isSelected: Int
)