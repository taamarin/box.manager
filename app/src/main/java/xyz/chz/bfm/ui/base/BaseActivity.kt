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
        val moduleVersionCode = ModuleManager.moduleVersionCode.toIntOrNull()
        if (moduleVersionCode == null) {
            val df = MakeDialog(
                StatusConnection.Error.str,
                getString(R.string.module_not_found),
                true,
                false
            )
            df.listener = this
            df.show(supportFragmentManager, "")
        } else {
            val minimumVersionCode = getString(R.string.min_module_vrsion).toIntOrNull()
            if (minimumVersionCode != null && moduleVersionCode < minimumVersionCode) {
                val df = MakeDialog(
                    StatusConnection.Loading.str,
                    String.format(
                        getString(R.string.update_module),
                        moduleVersionCode
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