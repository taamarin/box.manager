package xyz.chz.bfm.dialog

import androidx.fragment.app.DialogFragment

interface SettingDialogInterface {
    fun onLoading(dialog: DialogFragment)
    fun onAbout(dialog: DialogFragment)
    fun onUpdate(dialog: DialogFragment)
    fun onCheckIP(dialog: DialogFragment)
}