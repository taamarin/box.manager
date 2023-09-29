package xyz.chz.bfm.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.dialog.IMakeDialog
import xyz.chz.bfm.dialog.MakeDialog
import xyz.chz.bfm.enm.StatusConnection
import xyz.chz.bfm.util.modul.ModuleManager

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity(), IMakeDialog {
    private var state: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ModuleManager.moduleVersionCode == "") {
            val df = MakeDialog(
                StatusConnection.Error.str,
                getString(R.string.module_not_found),
                true,
                false
            )
            df.listener = this
            df.show(supportFragmentManager, "")
        } else {
            if (ModuleManager.moduleVersionCode.toInt() < getString(R.string.min_module_vrsion).toInt()) {
                val df = MakeDialog(
                    StatusConnection.Loading.str,
                    String.format(
                        getString(R.string.update_module),
                        ModuleManager.moduleVersionCode.toInt()
                    ),
                    true,
                    false
                )
                df.listener = this
                df.show(supportFragmentManager, "")
                state = 1
            }
        }
    }

    override fun onDialogPositiveButton(dialog: DialogFragment) {
        when (state) {
            1 -> {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.release_repo_url)))
                startActivity(intent)
                finish()
            }

            else -> {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.module_repo_url)))
                startActivity(intent)
                finish()
            }
        }
    }
}