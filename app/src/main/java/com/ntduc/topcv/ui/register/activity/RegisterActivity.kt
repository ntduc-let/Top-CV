package com.ntduc.topcv.ui.register.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.ntduc.contextutils.inflater
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityLoginBinding
import com.ntduc.topcv.databinding.ActivityRegisterBinding
import com.ntduc.topcv.ui.login.activity.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(inflater)
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

        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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