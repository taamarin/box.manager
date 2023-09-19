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
import xyz.chz.bfm.databinding.SettingDialogBinding
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.SettingCmd

class SettingDialog : MaterialDialogFragment() {

    private lateinit var binding: SettingDialogBinding

    lateinit var listener: SettingDialogInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = SettingDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        try {
            listener = context as SettingDialogInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("not cast")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val coreArray = arrayOf("clash", "sing-box", "xray", "v2fly")
        val procArray = arrayOf("off", "strict", "always")
        val clashTypeArray = arrayOf("premium", "meta")
        val netwrokArray = arrayOf("tproxy", "redirect", "mixed")
        val proxyArray = arrayOf("tun", "whitelist", "blacklist")

        enableDisable(!Util.isProxyed)
        binding.apply {
            with(SettingCmd) {

                coreSelector.apply {
                    buildSpinner(coreArray, this)
                    when (core) {
                        "clash" -> setSelection(0)
                        "sing-box" -> setSelection(1)
                        "xray" -> setSelection(2)
                        else -> setSelection(3)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                0 -> {
                                    clash1.visibility = View.VISIBLE
                                    clash2.visibility = View.VISIBLE
                                    clash5.visibility = View.VISIBLE
                                    clash6.visibility = View.VISIBLE
                                    clash7.visibility = View.VISIBLE
                                    clash8.visibility = View.VISIBLE
                                    setCore("\"clash\"")
                                }

                                1 -> {
                                    clash1.visibility = View.GONE
                                    clash2.visibility = View.GONE
                                    clash5.visibility = View.GONE
                                    clash6.visibility = View.GONE
                                    clash7.visibility = View.GONE
                                    clash8.visibility = View.GONE
                                    setCore("\"sing-box\"")
                                }

                                2 -> {
                                    clash1.visibility = View.GONE
                                    clash2.visibility = View.GONE
                                    clash5.visibility = View.GONE
                                    clash6.visibility = View.GONE
                                    clash7.visibility = View.GONE
                                    clash8.visibility = View.GONE
                                    setCore("\"xray\"")
                                }

                                else -> {
                                    clash1.visibility = View.GONE
                                    clash2.visibility = View.GONE
                                    clash5.visibility = View.GONE
                                    clash6.visibility = View.GONE
                                    clash7.visibility = View.GONE
                                    clash8.visibility = View.GONE
                                    setCore("\"v2fly\"")
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
                }

                spFindProc.apply {
                    buildSpinner(procArray, this)
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

                spClashType.apply {
                    buildSpinner(clashTypeArray, this)
                    setSelection(if (clashType == "premium") 0 else 1)
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            setClashType(if (p2 == 0) "premium" else "meta")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
                }

                spNetworkMode.apply {
                    buildSpinner(netwrokArray, this)
                    when (networkMode) {
                        "tproxy" -> setSelection(0)
                        "redirect" -> setSelection(1)
                        else -> setSelection(2)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setNetworkMode("redirect")
                                2 -> setNetworkMode("mixed")
                                else -> setNetworkMode("tproxy")
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                spProxyMode.apply {
                    buildSpinner(proxyArray, this)
                    when (proxyMode) {
                        "whitelist" -> setSelection(1)
                        "blacklist" -> setSelection(2)
                        else -> setSelection(0)
                    }
                    onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long
                        ) {
                            when (p2) {
                                1 -> setProxyMode("whitelist")
                                2 -> setProxyMode("blacklist")
                                else -> setProxyMode("tun")
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

                cbfakeIp.apply {
                    isChecked = fakeIp
                    setOnCheckedChangeListener { _, b ->
                        if (b) setFakeIp("fake-ip") else setFakeIp("redir-host")
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

                cbcgr.apply {
                    isChecked = cgr.toBoolean()
                    setOnCheckedChangeListener { _, b ->
                        if (b) setCgr("true") else setCgr("false")
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

                cbportDetect.apply {
                    isChecked = portDetect
                    setOnCheckedChangeListener { _, b ->
                        if (b) setPortDetect("true") else setPortDetect("false")
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
        cbportDetect.isEnabled = bo
        cbipv6.isEnabled = bo
        cbquic.isEnabled = bo
        cbcgr.isEnabled = bo
        cbgeo.isEnabled = bo
        cbsubs.isEnabled = bo
        cbgeodataMod.isEnabled = bo
        cbfakeIp.isEnabled = bo
        coreSelector.isEnabled = bo
        spProxyMode.isEnabled = bo
        spNetworkMode.isEnabled = bo
        spClashType.isEnabled = bo
        spFindProc.isEnabled = bo
    }
}