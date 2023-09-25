package xyz.chz.bfm.ui.converter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.databinding.ActivityConverterBinding
import xyz.chz.bfm.util.Util
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
        btnConvert.setOnClickListener {
            val str = textInput.text.toString()
            val spltStr = str.split("\n")
            val result = StringBuilder()
            for (x in spltStr) {
                result.appendLine(ConfigManager.importConfig(x))
            }
            tvResult.text = result
            tvResult.apply {
                setOnClickListener {
                    Util.copyToClipboard(this@ConverterActivity, result.toString())
                }
            }
            toast("Click result for copy config", this@ConverterActivity)
        }
    }

}