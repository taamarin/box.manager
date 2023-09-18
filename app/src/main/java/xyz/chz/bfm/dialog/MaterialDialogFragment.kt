package xyz.chz.bfm.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.chz.bfm.R

open class MaterialDialogFragment: DialogFragment() {

    private var dialogView: View? = null

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_background).apply {
            dialogView = onCreateView(LayoutInflater.from(requireContext()), null, savedInstanceState)

            dialogView?.let { onViewCreated(it, savedInstanceState) }
            setView(dialogView)
        }.create()
    }

    override fun getView(): View? {
        return dialogView
    }

}