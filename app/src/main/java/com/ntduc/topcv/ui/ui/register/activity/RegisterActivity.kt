package com.ntduc.topcv.ui.ui.register.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ActivityRegisterBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
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

        binding.btnRegister.setOnClickListener {
            clickRegisterAccount()
        }
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[RegisterActivityVM::class.java]
    }

    private fun clickRegisterAccount() {
        if (checkInformation()) {
            if (binding.chkPolicy.isChecked) {
                val account = Account(
                    account = binding.edtEmail.text.toString(),
                    hash = binding.edtPassword.text.toString()
                )
                registerAccount(account)
            } else {
                shortToast("Bạn chưa đồng ý với Điều khoản dịch vụ và Chính sách bảo mật")
            }
        }
    }

    private fun registerAccount(account: Account) {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        viewModel.registerAccount(
            context = this,
            account = account,
            callApiListener = object : CallApiListener {
                override fun onSuccess(userInfo: UserInfo?) {
                    if (userInfo != null) {
                        createDataAccount(userInfo)
                    } else {
                        loadingDialog?.dismiss()
                    }
                }

                override fun onError(e: Throwable) {
                    loadingDialog?.dismiss()
                    shortToast("Hệ thống không phản hồi hoặc không có kết nối Internet")
                }
            })
    }

    private fun createDataAccount(userInfo: UserInfo) {
        val db = Firebase.firestore

        val account = UserDB(userInfo = userInfo, name = binding.edtName.text.toString())

        db.collection(userInfo._id!!).document("account")
            .set(account)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                loadingDialog?.dismiss()

                shortToast("Tạo tài khoản thành công")
                startLogin()
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun startLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(KEY_EMAIL, binding.edtEmail.text.toString())
        intent.putExtra(KEY_PASSWORD, binding.edtPassword.text.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun checkInformation(): Boolean {
        if (binding.edtEmail.text.trim().isEmpty()
            || !Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.trim()).matches()
            || binding.edtName.text.trim().isEmpty()
            || binding.edtName.text.trim().length !in 2..50
            || binding.edtPassword.text.isEmpty()
            || binding.edtPassword.text.length !in 6..25
            || binding.edtPassword2.text.isEmpty()
            || binding.edtPassword.text.toString() != binding.edtPassword2.text.toString()
        ) {
            if (binding.edtEmail.text.trim().isEmpty()) {
                binding.notificationEmail.text = "Email không được để trống"
                binding.notificationEmail.visibility = View.VISIBLE
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.trim()).matches()) {
                binding.notificationEmail.text = "Định dạng Email không hợp lệ"
                binding.notificationEmail.visibility = View.VISIBLE
            } else {
                binding.notificationEmail.visibility = View.GONE
            }

            if (binding.edtName.text.trim().isEmpty()) {
                binding.notificationName.text = "Tên không được để trống"
                binding.notificationName.visibility = View.VISIBLE
            } else if (binding.edtName.text.trim().length !in 2..50) {
                binding.notificationName.text = "Tên phải từ 2 đến 50 ký tự"
                binding.notificationName.visibility = View.VISIBLE
            } else {
                binding.notificationName.visibility = View.GONE
            }
            if (binding.edtPassword.text.isEmpty()) {
                binding.notificationPassword.text = "Mật khẩu không được để trống"
                binding.notificationPassword.visibility = View.VISIBLE
            } else if (binding.edtPassword.text.length !in 6..25) {
                binding.notificationPassword.text = "Mật khẩu hợp lệ từ 6 - 25 ký tự"
                binding.notificationPassword.visibility = View.VISIBLE
            } else {
                binding.notificationPassword.visibility = View.GONE
            }
            if (binding.edtPassword2.text.isEmpty()) {
                binding.notificationPassword2.text = "Vui lòng xác nhận lại mật khẩu"
                binding.notificationPassword2.visibility = View.VISIBLE
            } else if (binding.edtPassword.text.toString() != binding.edtPassword2.text.toString()) {
                binding.notificationPassword2.text = "Mật khẩu không khớp"
                binding.notificationPassword2.visibility = View.VISIBLE
            } else {
                binding.notificationPassword2.visibility = View.GONE
            }
            return false
        }

        binding.notificationEmail.visibility = View.GONE
        binding.notificationName.visibility = View.GONE
        binding.notificationPassword.visibility = View.GONE
        binding.notificationPassword2.visibility = View.GONE
        return true
    }

    companion object {
        const val KEY_EMAIL = "KEY_EMAIL"
        const val KEY_PASSWORD = "KEY_PASSWORD"
    }

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterActivityVM
    private var loadingDialog: LoadingDialog? = null
}