package xyz.chz.bfm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.FragmentMainBinding
import xyz.chz.bfm.dialog.MakeDialog
import xyz.chz.bfm.dialog.MakeDialogInterface
import xyz.chz.bfm.dialog.SettingDialog
import xyz.chz.bfm.dialog.SettingDialogInterface
import xyz.chz.bfm.enm.StatusConnection
import xyz.chz.bfm.ui.core.CoreActivity
import xyz.chz.bfm.ui.model.MainViewModel
import xyz.chz.bfm.util.OkHttpHelper
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.SettingCmd
import xyz.chz.bfm.util.command.TermCmd
import xyz.chz.bfm.util.modul.ModuleManager
import xyz.chz.bfm.util.moduleVer
import xyz.chz.bfm.util.setColorBackground
import xyz.chz.bfm.util.setImage
import xyz.chz.bfm.util.setTextHtml
import xyz.chz.bfm.util.toast
import java.io.IOException

@AndroidEntryPoint
class MainFragment : Fragment(), SettingDialogInterface, MakeDialogInterface {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var state: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
        configEditor()
        checkCore()
    }

    private fun setProxyCard(status: String) = with(binding) {
        val strapps: String = if (TermCmd.appidList.size == 0) String.format(
            getString(R.string.no_apps_count_list),
            SettingCmd.proxyMode
        )
        else String.format(
            getString(R.string.apps_count_list), TermCmd.appidList.size, SettingCmd.proxyMode
        )

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
        viewModel.log.observe(viewLifecycleOwner) {
            tvLog.text = setTextHtml(it)
        }
    }

    private fun configEditor() = with(binding.fbConfig) {
        setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_configHelperFragment)
        }
    }

    private fun checkCore() = with(binding.imgModule) {
        setOnClickListener {
            startActivity(Intent(context, CoreActivity::class.java))
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
        binding.prgLoadingTop.visibility = View.GONE
        showRes(
            "https://raw.githubusercontent.com/taamarin/box_for_magisk/master/update.json",
            "Updater",
            0
        )
        state = 0
    }

    override fun onCheckIP(dialog: DialogFragment) {
        binding.prgLoadingTop.visibility = View.VISIBLE
        showRes("http://ip-api.com/json", "MyIP", 1)
        state = 1
    }

    override fun onDialogPositiveButton(dialog: DialogFragment) = when (state) {
        1 -> {}
        else -> {}
    }


    private fun showRes(url: String, title: String, state: Int) {
        binding.prgLoadingTop.visibility = View.VISIBLE
        val request = OkHttpHelper()
        request.getRawTextFromURL(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Util.runOnUiThread {
                    toast(e.message!!, requireActivity())
                    binding.prgLoadingTop.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jo = JSONObject(response.body!!.string())
                    var res: String? = ""
                    res = if (state == 1) {
                        "IP: ${jo.getString("query")}\n" +
                                "ISP: ${jo.getString("isp")}\n" +
                                "TZ: ${jo.getString("timezone")}\n" +
                                "C: ${jo.getString("country")}\n" +
                                "City: ${jo.getString("city")}\n"
                    } else {
                        if (jo.getString("versionCode")
                                .toInt() > ModuleManager.moduleVersionCode.toInt()
                        ) {
                            "Update available\nDo you want update now?"
                        } else {
                            "No update found"
                        }
                    }
                    val df = MakeDialog(title, res)
                    df.listener = this@MainFragment
                    df.show(requireActivity().supportFragmentManager, "")
                } catch (e: JSONException) {
                    Log.d(MainFragment().tag, e.message!!)
                }
                Util.runOnUiThread {
                    binding.prgLoadingTop.visibility = View.GONE
                }
            }
        })
    }

}