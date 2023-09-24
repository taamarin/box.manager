package xyz.chz.bfm.ui.converter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.databinding.ActivityConverterBinding

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
            val result = "Not Yet"
            tvResult.text = result
        }
    }

}