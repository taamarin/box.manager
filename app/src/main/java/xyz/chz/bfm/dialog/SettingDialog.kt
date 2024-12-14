package xyz.chz.bfm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.SettingDialogBinding
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.SettingCmd

class SettingDialog : MaterialDialogFragment() {

    private lateinit var binding: SettingDialogBinding

    lateinit var listener: ISettingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = SettingDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        try {
            listener = context as ISettingDialog
        } catch (e: ClassCastException) {
            throw ClassCastException("not cast")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableDisable(!Util.isProxyed)
        binding.apply {
            with(SettingCmd) {

                coreSelector.apply {
                    buildSpinner(resources.getStringArray(R.array.core_array), this)
                    setSelection(
                        when (core) {
                            "clash" -> 0
                            "sing-box" -> 1
                            "xray" -> 2
                            "hysteria" -> 3
                            else -> 4
                        }
                    )
                    
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val isClashSelected = position == 0
                            val visibility = if (isClashSelected) View.VISIBLE else View.GONE
                            
                            clash1.visibility = visibility
                            clash2.visibility = visibility
                            clash4.visibility = visibility
                            clash5.visibility = visibility
                            clash6.visibility = visibility
                            clash7.visibility = visibility
                            clash8.visibility = visibility
                            
                            setCore = when (position) {
                                0 -> "clash"
                                1 -> "sing-box"
                                2 -> "xray"
                                3 -> "hysteria"
                                else -> "v2fly"
                            }
                        }
                
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }

                spFindProc.apply {
                    buildSpinner(resources.getStringArray(R.array.proc_array), this)
                    when (findProc) {
                        "off" -> setSelection(0)
                        "strict" -> setSelection(1)
                        else -> setSelection(2)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setFindProc("strict")
                                2 -> setFindProc("always")
                                else -> setFindProc("off")
                            }
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                spFindConf.apply {
                    buildSpinner(resources.getStringArray(R.array.conf_array), this)
                    when (findConf) {
                        "config.yaml" -> setSelection(0)
                        "config2.yaml" -> setSelection(1)
                        else -> setSelection(2)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setFindConf("config2.yaml")
                                2 -> setFindConf("config3.yaml")
                                else -> setFindConf("config.yaml")
                            }
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                spClashType.apply {
                    buildSpinner(resources.getStringArray(R.array.clash_core_array), this)
                    setSelection(if (clashType == "premium") 0 else 1)
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            setClashType(if (p2 == 0) "premium" else "mihomo")
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
                }

                spNetworkMode.apply {
                    buildSpinner(resources.getStringArray(R.array.network_array), this)
                    when (networkMode) {
                        "tproxy" -> setSelection(0)
                        "redirect" -> setSelection(1)
                        "enhance" -> setSelection(2)
                        "mixed" -> setSelection(3)
                        else -> setSelection(4)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setNetworkMode("redirect")
                                2 -> setNetworkMode("enhance")
                                3 -> setNetworkMode("mixed")
                                4 -> setNetworkMode("tun")
                                else -> setNetworkMode("tproxy")
                            }
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                spProxyMode.apply {
                    buildSpinner(resources.getStringArray(R.array.proxy_array), this)
                    when (proxyMode) {
                        "whitelist" -> setSelection(0)
                        else -> setSelection(1)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setProxyMode("blacklist")
                                else -> setProxyMode("whitelist")
                            }
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                cbunifiedDelay.apply {
                    isChecked = unified
                    setOnCheckedChangeListener { _, b ->
                        if (b) setUnified("true") else setUnified("false")
                    }
                }

                cbsnifferrs.apply {
                    isChecked = sniffer
                    setOnCheckedChangeListener { _, b ->
                        if (b) setSniffer("true") else setSniffer("false")
                    }
                }

                cbredirHost.apply {
                    isChecked = redirHost
                    setOnCheckedChangeListener { _, b ->
                        if (b) setRedirHost("redir-host") else setRedirHost("fake-ip")
                    }
                }

                cbgeodataMod.apply {
                    isChecked = geodata
                    setOnCheckedChangeListener { _, b ->
                        if (b) setGeodata("true") else setGeodata("false")
                    }
                }

                cbsubs.apply {
                    isChecked = subs.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setSubs("true") else setSubs("false")
                    }
                }

                cbgeo.apply {
                    isChecked = geo.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setGeo("true") else setGeo("false")
                    }
                }

                cbmemcg.apply {
                    isChecked = memcg.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setMemcg("true") else setMemcg("false")
                    }
                }

                cbblkio.apply {
                    isChecked = blkio.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setBlkio("true") else setBlkio("false")
                    }
                }

                cbcpuset.apply {
                    isChecked = cpuset.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setCpuset("true") else setCpuset("false")
                    }
                }

                cbquic.apply {
                    isChecked = quic
                    setOnCheckedChangeListener { _, b ->
                        if (b) setQuic("enable") else setQuic("disable")
                    }
                }

                cbipv6.apply {
                    isChecked = ipv6
                    setOnCheckedChangeListener { _, b ->
                        if (b) setIpv6("true") else setIpv6("false")
                    }
                }

                cbcron.apply {
                    isChecked = cron.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setCron("true") else setCron("false")
                    }
                }

                aboutApp.setOnClickListener {
                    listener.onAbout(this@SettingDialog)
                }

                checkIp.setOnClickListener {
                    listener.onCheckIP(this@SettingDialog)
                    this@SettingDialog.dismiss()
                }

                checkModule.setOnClickListener {
                    listener.onUpdate(this@SettingDialog)
                    this@SettingDialog.dismiss()
                }
            }
        }
        listener.onLoading(this@SettingDialog)
    }

    private fun buildSpinner(arr: Array<String>, spin: Spinner) {
        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arr)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin.adapter = aa
    }

    private fun enableDisable(bo: Boolean) = with(binding) {
        cbunifiedDelay.isEnabled = bo
        cbsnifferrs.isEnabled = bo
        cbcron.isEnabled = bo
        cbipv6.isEnabled = bo
        cbquic.isEnabled = bo
        cbmemcg.isEnabled = bo
        cbblkio.isEnabled = bo
        cbcpuset.isEnabled = bo
        cbgeo.isEnabled = bo
        cbsubs.isEnabled = bo
        cbgeodataMod.isEnabled = bo
        cbredirHost.isEnabled = bo
        coreSelector.isEnabled = bo
        spProxyMode.isEnabled = bo
        spNetworkMode.isEnabled = bo
        spClashType.isEnabled = bo
        spFindProc.isEnabled = bo
        spFindConf.isEnabled = bo
    }
}