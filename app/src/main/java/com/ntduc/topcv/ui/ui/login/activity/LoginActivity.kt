package com.ntduc.topcv.ui.ui.login.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ActivityLoginBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.register.activity.RegisterActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(inflater)
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

        binding.btnSignOut.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            clickLoginAccount()
        }
    }

    private fun initView() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        viewModel = ViewModelProvider(this)[LoginActivityVM::class.java]
        db = Firebase.firestore

        val emailRegister = intent.getStringExtra(RegisterActivity.KEY_EMAIL)
        val passwordRegister = intent.getStringExtra(RegisterActivity.KEY_PASSWORD)
        if (emailRegister != null && passwordRegister != null){
            binding.edtEmail.setText(emailRegister)
            binding.edtPassword.setText(passwordRegister)
        }
    }

    private fun clickLoginAccount() {
        if (checkInformation()) {
            val account = Account(
                account = binding.edtEmail.text.toString(),
                hash = binding.edtPassword.text.toString()
            )
            loginAccount(account)
        }
    }

    private fun loginAccount(account: Account) {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        viewModel.loginAccount(
            context = this,
            account = account,
            callApiListener = object : CallApiListener {
                override fun onSuccess(userInfo: UserInfo?) {
                    if (userInfo != null) {
                        getDataAccount(userInfo)
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

    private fun getDataAccount(userInfo: UserInfo) {
        val docRef = db.collection(userInfo._id!!).document("account")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("ntduc_debug", "DocumentSnapshot data: ${document.data}")
                    val account = document.toObject<UserDB>()
                    if (account != null) {
                        loadingDialog?.dismiss()

                        shortToast("Đăng nhập thành công")
                        startHome(account)
                    } else {
                        recreateDataAccount(userInfo)
                    }

                } else {
                    loadingDialog?.dismiss()
                    shortToast("Hệ thống không phản hồi hoặc không có kết nối Internet")
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "get failed with $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun startHome(account: UserDB) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(KEY_USER_DB, account)
        startActivity(intent)
        finish()
    }

    private fun recreateDataAccount(userInfo: UserInfo) {
        val account = UserDB(userInfo = userInfo, name = userInfo.account)

        db.collection(userInfo._id!!).document("account")
            .set(account)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                loadingDialog?.dismiss()

                shortToast("Đăng nhập thành công")
                startHome(account)
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun checkInformation(): Boolean {
        if (binding.edtEmail.text.trim().isEmpty()
            || !Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.trim()).matches()
            || binding.edtPassword.text.isEmpty()
            || binding.edtPassword.text.length !in 6..25
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
            if (binding.edtPassword.text.isEmpty()) {
                binding.notificationPassword.text = "Mật khẩu không được để trống"
                binding.notificationPassword.visibility = View.VISIBLE
            } else if (binding.edtPassword.text.length !in 6..25) {
                binding.notificationPassword.text = "Mật khẩu hợp lệ từ 6 - 25 ký tự"
                binding.notificationPassword.visibility = View.VISIBLE
            } else {
                binding.notificationPassword.visibility = View.GONE
            }
            return false
        }

        binding.notificationEmail.visibility = View.GONE
        binding.notificationPassword.visibility = View.GONE
        return true
    }

    companion object {
        const val KEY_USER_DB = "KEY_USER_DB"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginActivityVM
    private lateinit var db: FirebaseFirestore
    private var loadingDialog: LoadingDialog? = null
}