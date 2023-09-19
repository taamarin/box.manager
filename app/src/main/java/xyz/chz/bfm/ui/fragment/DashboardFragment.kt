package xyz.chz.bfm.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.databinding.FragmentDashboardBinding
import xyz.chz.bfm.util.command.TermCmd

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            dbWebview.loadUrl("${TermCmd.linkDasboard()}/ui/#/proxies")
            with(dbWebview.settings) {
                domStorageEnabled = true
                databaseEnabled = true
                allowContentAccess = true
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                dbWebview.webViewClient = WebViewClient()
            }
        }
    }
}