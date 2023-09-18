package xyz.chz.bfm.dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.chz.bfm.databinding.CustomDialogBinding

class MakeDialog(
    private val strTitle: String? = "",
    private val strMsg: String? = "",
    private val isClick: Boolean? = false
) : MaterialDialogFragment() {
    private lateinit var binding: CustomDialogBinding

    lateinit var listener: MakeDialogInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = CustomDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            listener = activity as MakeDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("not cast")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvTitle.text = strTitle
            tvMessage.text = strMsg
            btnOk.setOnClickListener {
                if (isClick == true) {
                    listener.onDialogPositiveButton(this@MakeDialog)
                }
                this@MakeDialog.dismiss()
            }
        }
    }

}