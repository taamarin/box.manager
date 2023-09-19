package xyz.chz.bfm.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.dialog.MakeDialog
import xyz.chz.bfm.dialog.MakeDialogInterface
import xyz.chz.bfm.enm.StatusConnection
import xyz.chz.bfm.util.modul.ModuleManager

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity(), MakeDialogInterface {
    private var state: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()
        if (ModuleManager.moduleVersionCode == "") {
            val df = MakeDialog(StatusConnection.Error.str, "Module Not Found", true, false)
            df.listener = this
            df.show(supportFragmentManager, "")
        } else {
            if (ModuleManager.moduleVersionCode.toInt() < 20230814) {
                val df = MakeDialog(StatusConnection.Loading.str, "Please Upgrade Your Module :)", true, false)
                df.listener = this
                df.show(supportFragmentManager, "")
                state = 1
            }
        }
    }

    fun setupTheme() {
    }

    override fun onDialogPositiveButton(dialog: DialogFragment) {
        when (state) {
            1 -> {}
            else -> {}
        }
    }
}