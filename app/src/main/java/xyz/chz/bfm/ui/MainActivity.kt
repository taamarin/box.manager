package xyz.chz.bfm.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import xyz.chz.bfm.R
import xyz.chz.bfm.databinding.ActivityMainBinding
import xyz.chz.bfm.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navBottom
        val navController = findNavController(R.id.nav_host_fragment_container)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.nav_dashboard) this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            if (dest.id == R.id.configHelperFragment) navView.visibility =
                View.GONE else navView.visibility = View.VISIBLE
        }
    }

}