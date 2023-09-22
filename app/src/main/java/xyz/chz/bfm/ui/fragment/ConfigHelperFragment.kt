package xyz.chz.bfm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import xyz.chz.bfm.databinding.FragmentConfigHelperBinding
import xyz.chz.bfm.util.Util
import xyz.chz.bfm.util.command.TermCmd
import xyz.chz.bfm.util.myReplacer
import xyz.chz.bfm.util.toast

class ConfigHelperFragment : Fragment() {

    private lateinit var binding: FragmentConfigHelperBinding

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
        showDivider = true
        showMinimap = false
    }

    private fun initEditor() = with(binding) {
        myEditor.text = TermCmd.getConfig()
    }

    private fun editorApply() = with(binding) {
        saveConfig.apply {
            setOnClickListener {
                val input = myEditor.text
                TermCmd.saveConfig(requireActivity(), input) {
                    Util.runOnUiThread {
                        if (it)
                            toast("Success", requireActivity())
                        else
                            toast("Failed", requireActivity())
                    }
                }
            }
        }
    }

    private fun readRawFile(@RawRes id: Int): String {
        return resources.openRawResource(id).bufferedReader().readText()
    }

}