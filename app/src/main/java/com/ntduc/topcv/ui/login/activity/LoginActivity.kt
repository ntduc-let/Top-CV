package com.ntduc.topcv.ui.login.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.ntduc.contextutils.inflater
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityLoginBinding
import com.ntduc.topcv.ui.register.activity.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnSignOut.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun initView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}