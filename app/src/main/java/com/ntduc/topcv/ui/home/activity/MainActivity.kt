package com.ntduc.topcv.ui.home.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.jobFragment,
                R.id.CVFragment,
                R.id.profileFragment
            ).build()
        setupWithNavController(binding.bnvMain, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    var time = 0L
    override fun onBackPressed() {
        val backStackEntryCount = navHostFragment.childFragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            if (!navController.popBackStack()) {
                finish()
            }
            return
        } else {
            if (System.currentTimeMillis() - time < 2000) {
                finish()
            } else {
                shortToast(R.string.press_again_to_exit)
                time = System.currentTimeMillis()
            }
        }
    }
}