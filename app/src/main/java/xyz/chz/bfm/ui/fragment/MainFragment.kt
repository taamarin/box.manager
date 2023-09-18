package xyz.chz.bfm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.FragmentMainBinding
import xyz.chz.bfm.enm.StatusConnection
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.ThermUtil
import xyz.chz.bfm.util.moduleVer
import xyz.chz.bfm.util.setColorBackground
import xyz.chz.bfm.util.setImage

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
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
                    ThermUtil.stop {
                        Util.runOnUiThread {
                            if (it) {
                                setProxyCard(StatusConnection.Disabled.str)
                                Util.isProxyed = false
                            } else {
                                setProxyCard(StatusConnection.Error.str)
                                Util.isProxyed = true
                            }
                            v.isClickable = true
                        }
                    }
                } else {
                    ThermUtil.start {
                        Util.runOnUiThread {
                            if (it) {
                                setProxyCard(StatusConnection.Enabled.str)
                                Util.isProxyed = true
                            } else {
                                setProxyCard(StatusConnection.Error.str)
                                Util.isProxyed = false
                            }
                            v.isClickable = true
                        }
                    }
                }
            }
        }
    }

    private fun setProxyCard(status: String) = with(binding) {
        when (status) {
            StatusConnection.Enabled.str -> {
                statusTitle.text = StatusConnection.Enabled.str
                proxy.setColorBackground("#6fa251")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Disabled.str -> {
                statusTitle.text = StatusConnection.Disabled.str
                proxy.setColorBackground("#87afc7")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Loading.str -> {
                statusTitle.text = StatusConnection.Loading.str
                proxy.setColorBackground("#478fec")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            StatusConnection.Error.str -> {
                statusTitle.text = StatusConnection.Error.str
                proxy.setColorBackground("#f35e5e")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }

            else -> {
                statusTitle.text = StatusConnection.Unknown.str
                proxy.setColorBackground("#26b545")
                statusIcon.setImage(R.drawable.ic_app)
                statusSummary.moduleVer()
            }
        }
    }

}