package xyz.chz.bfm.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.FragmentConfigHelperBinding
import xyz.chz.bfm.ui.converter.ConverterActivity
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.TermCmd
import xyz.chz.bfm.util.terminal.TerminalHelper.execRootCmd
import xyz.chz.bfm.util.toast


class ConfigHelperFragment : Fragment() {

    private lateinit var binding: FragmentConfigHelperBinding
    private var statePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigHelperBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditor()
        initEditor()
        editorApply()
    }

    private fun setupEditor() = with(binding.myEditor) {
        languageRuleBook = null
        lineNumberGenerator = { l ->
            (1..l).map { " $it " }
        }
        editable = true
        showDivider = false
        showMinimap = false
    }

    private fun initEditor() = with(binding) {
        dialogConfig()
    }

    private fun editorApply() = with(binding) {
        fbSave.apply {
            setOnClickListener {
                dialogSave()
            }
        }

        fbConverter.apply {
            setOnClickListener {
                val intent = Intent(context, ConverterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun dialogConfig() {
        val builder = AlertDialog.Builder(
            requireActivity(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_background
        )
        builder.setCancelable(false)
        val items = TermCmd.proxyProviderPath.toTypedArray()
        builder.setItems(items) { _, w ->
            val configSlected = execRootCmd("cat ${items[w]}")
            if (configSlected.isNotEmpty()) {
                binding.myEditor.text = configSlected
            } else {
                toast("Empty file", requireActivity())
            }
            statePath = items[w]

        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun dialogSave() {
        val builder = AlertDialog.Builder(
            requireActivity(),
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_background
        )
        builder.setTitle("Save")
        builder.setMessage("Do you want save config now ?")
        builder.setPositiveButton("yes") { _, _ ->
            val input = binding.myEditor.text
            TermCmd.saveConfig(requireActivity(), input, statePath!!) {
                Util.runOnUiThread {
                    if (it)
                        toast(requireActivity().getString(R.string.success), requireActivity())
                    else
                        toast(requireActivity().getString(R.string.failed), requireActivity())
                }
            }
        }
        builder.setNegativeButton("cancel") { d, _ ->
            d.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun readRawFile(@RawRes id: Int): String {
        return resources.openRawResource(id).bufferedReader().readText()
    }

}