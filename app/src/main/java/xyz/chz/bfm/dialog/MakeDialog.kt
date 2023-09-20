package xyz.chz.bfm.dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.chz.bfm.databinding.CustomDialogBinding

class MakeDialog(
    private val strTitle: String? = "",
    private val strMsg: String? = "",
    private val isClick: Boolean? = false,
    private val canCancel: Boolean? = true
) : MaterialDialogFragment() {
    private lateinit var binding: CustomDialogBinding

    lateinit var listener: MakeDialogInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CustomDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        try {
            listener = context as MakeDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("not cast")
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            listener = context as MakeDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("not cast")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            isCancelable = canCancel!!
            tvTitle.text = strTitle
            tvMessage.text = strMsg
            btnOk.setOnClickListener {
                if (isClick!!) listener.onDialogPositiveButton(this@MakeDialog)
                this@MakeDialog.dismiss()
            }
        }
    }

}