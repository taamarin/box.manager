package xyz.chz.bfm.ui.converter

import android.os.Bundle
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.ActivityConverterBinding
import xyz.chz.bfm.util.copyToClipboard
import xyz.chz.bfm.util.isValidCheck
import xyz.chz.bfm.util.removeEmptyLines
import xyz.chz.bfm.util.toast

@AndroidEntryPoint
class ConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConverterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupConfig()
    }

    private fun setupConfig() = with(binding) {
        var isFull = false
        var isSing = false
        fullClashConfig.setOnCheckedChangeListener { _, b ->
            isFull = b
            isSing = false
            fullSingConfig.isChecked = false
        }
        fullSingConfig.setOnCheckedChangeListener { _, b ->
            if (b) {
                isSing = true
                isFull = false
                fullClashConfig.isChecked = false
            }
        }
        btnConvert.setOnClickListener {
            if (textInput.isValidCheck()) {
                val str = textInput.removeEmptyLines()
                val spltStr = str.split("\n")
                val result = StringBuilder()
                for (x in spltStr) {
                    result.appendLine(
                        "${
                            if (isSing) ConfigManager.importConfig(
                                x,
                                false
                            ) else ConfigManager.importConfig(x, true)
                        },"
                    )
                }
                if (isFull)
                    tvResult.text =
                        ConfigManager.fullClashSimple(
                            result.toString(),
                            readRawFile(R.raw.clashtemplate)
                        )
                else
                    tvResult.text = if (isSing) ConfigManager.fullSingSimple(
                        result.toString(),
                        readRawFile(R.raw.singboxtemplate)
                    ) else result
                tvResult.apply {
                    setOnClickListener {
                        copyToClipboard(this@ConverterActivity)
                    }
                }
                toast("Click result for copy config", this@ConverterActivity)
            } else toast("???????????", this@ConverterActivity)
        }
    }


    private fun readRawFile(@RawRes id: Int): String {
        return resources.openRawResource(id).bufferedReader().readText()
    }

}