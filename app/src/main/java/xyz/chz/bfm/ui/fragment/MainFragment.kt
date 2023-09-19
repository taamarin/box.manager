package xyz.chz.bfm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.FragmentMainBinding
import xyz.chz.bfm.dialog.MakeDialog
import xyz.chz.bfm.dialog.MakeDialogInterface
import xyz.chz.bfm.dialog.SettingDialog
import xyz.chz.bfm.dialog.SettingDialogInterface
import xyz.chz.bfm.enm.StatusConnection
import xyz.chz.bfm.ui.model.MainViewModel
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.SettingCmd
import xyz.chz.bfm.util.command.TermCmd
import xyz.chz.bfm.util.moduleVer
import xyz.chz.bfm.util.setColorBackground
import xyz.chz.bfm.util.setImage

@AndroidEntryPoint
class MainFragment : Fragment(), SettingDialogInterface, MakeDialogInterface {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.dataLog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Util.isProxyed) {
            setProxyCard(StatusConnection.Enabled.str)
        } else {
            setProxyCard(StatusConnection.Disabled.str)
        }
        binding.apply {
            proxy.setOnClickListener { v ->
                setProxyCard(StatusConnection.Loading.str)
                v.isClickable = false
                if (Util.isProxyed) {
                    TermCmd.stop {
                        Util.runOnUiThread {
                            Util.isProxyed = if (it) {
                                setProxyCard(StatusConnection.Disabled.str)
                                false
                            } else {
                                setProxyCard(StatusConnection.Error.str)
                                true
                            }
                            v.isClickable = true
                        }
                    }
                } else {
                    TermCmd.start {
                        Util.runOnUiThread {
                            Util.isProxyed = if (it) {
                                setProxyCard(StatusConnection.Enabled.str)
                                true
                            } else {
                                setProxyCard(StatusConnection.Error.str)
                                false
                            }
                            v.isClickable = true
                        }
                    }
                }
            }
        }
        setupLog()
        settings()
    }

    private fun setProxyCard(status: String) = with(binding) {
        val strapps =
            String.format(getString(R.string.apps_count_list), TermCmd.appidList.size, SettingCmd.proxyMode)
        when (status) {
            StatusConnection.Enabled.str -> {
                statusTitle.text = StatusConnection.Enabled.str
                tvApps.text = strapps
                proxy.setColorBackground("#6fa251")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Disabled.str -> {
                statusTitle.text = StatusConnection.Disabled.str
                tvApps.text = strapps
                proxy.setColorBackground("#87afc7")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Loading.str -> {
                statusTitle.text = StatusConnection.Loading.str
                tvApps.text = strapps
                proxy.setColorBackground("#478fec")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Error.str -> {
                statusTitle.text = StatusConnection.Error.str
                tvApps.text = strapps
                proxy.setColorBackground("#f35e5e")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            else -> {
                statusTitle.text = StatusConnection.Unknown.str
                tvApps.text = strapps
                proxy.setColorBackground("#26b545")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }
        }
    }

    private fun settings() = with(binding) {
        with(fbSetting) {
            setOnClickListener {
                visibility = View.GONE
                prgLoading.visibility = View.VISIBLE
                val sd = SettingDialog()
                sd.listener = this@MainFragment
                sd.show(requireActivity().supportFragmentManager, "")
            }
        }
    }

    private fun setupLog() = with(binding) {
        viewModel.log.observe(viewLifecycleOwner) { data ->
            tvLog.text = data
        }
    }

    override fun onLoading(dialog: DialogFragment) {
        binding.fbSetting.visibility = View.VISIBLE
        binding.prgLoading.visibility = View.GONE
    }

    override fun onAbout(dialog: DialogFragment) {
        // dont remove this :)
        val df = MakeDialog("About", "App: t.me/chetoosz\nModule: t.me/taamarin")
        df.listener = this@MainFragment
        df.show(requireActivity().supportFragmentManager, "")
    }

    override fun onUpdate(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }

    override fun onCheckIP(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }

    override fun onDialogPositiveButton(dialog: DialogFragment) {
        TODO("Not yet implemented")
    }

}