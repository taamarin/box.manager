package xyz.chz.bfm.ui.converter

import android.os.Bundle
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.ActivityConverterBinding
import xyz.chz.bfm.util.copyToClipboard
import xyz.chz.bfm.util.isValidCheck
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
        fullClashConfig.setOnCheckedChangeListener { _, b ->
            isFull = b
        }
        btnConvert.setOnClickListener {
            if (textInput.isValidCheck()) {
                val str = textInput.text.toString()
                val spltStr = str.split("\n")
                val result = StringBuilder()
                for (x in spltStr) {
                    result.appendLine(ConfigManager.importConfig(x, false))
                }
                if (isFull)
                    tvResult.text =
                        ConfigManager.fullClashSimple(
                            result.toString(),
                            readRawFile(R.raw.clashtemplate)
                        )
                else
                    tvResult.text = result
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