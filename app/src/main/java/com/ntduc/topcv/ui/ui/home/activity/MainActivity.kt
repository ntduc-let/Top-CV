package com.ntduc.topcv.ui.ui.home.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityMainBinding
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initView()
        initData()
    }

    private fun initData() {
        val userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LoginActivity.KEY_USER_DB, UserDB::class.java)
        } else {
            intent.getParcelableExtra(LoginActivity.KEY_USER_DB) as UserDB?
        }

        if (userDB != null){
            viewModel.userDB.value = userDB
        }
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

        viewModel = ViewModelProvider(this)[MainActivityVM::class.java]
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

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityVM
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
}