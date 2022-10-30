package com.ntduc.topcv.ui.ui.account.information.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityAccountInformationBinding
import com.ntduc.topcv.databinding.FragmentJobBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.account.change_password.activity.ChangePasswordActivity
import com.ntduc.topcv.ui.ui.account.information.dialog.LogoutDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.GroupJobAdapter
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity
import com.ntduc.topcv.ui.utils.Prefs

class AccountInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.btnChangePassword.setOnClickListener {
            changePWLauncher.launch(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val dialog = LogoutDialog()
            dialog.setClickLogoutListener {
                mPrefs!!.saveLogin(false)
                val intent = Intent(this, MainActivity::class.java)
                setResult(RESULT_LOGOUT, intent)
                finish()
            }
            dialog.show(supportFragmentManager, "LogoutDialog")
        }
    }

    private fun initData() {
        mPrefs = Prefs(this)
        viewModel = ViewModelProvider(this)[AccountInformationActivityVM::class.java]
        db = Firebase.firestore

        if (mPrefs!!.email != null && mPrefs!!.password != null) {
            val account = Account(account = mPrefs!!.email, hash = mPrefs!!.password)

            viewModel.loginAccount(
                account = account,
                callApiListener = object : CallApiListener {
                    override fun onSuccess(userInfo: UserInfo?) {
                        if (userInfo != null) {
                            getDataAccount(userInfo)
                        } else {
                            shortToast("Có lỗi xảy ra, vui lòng thử lại")
                            mPrefs!!.saveLogin(false)
                            restartApp()
                        }
                    }

                    override fun onError(e: Throwable) {
                        shortToast("Có lỗi xảy ra, vui lòng thử lại")
                        mPrefs!!.saveLogin(false)
                        restartApp()
                    }
                })
        } else {
            shortToast("Có lỗi xảy ra, vui lòng thử lại")
            mPrefs!!.saveLogin(false)
            restartApp()
        }
    }

    private fun initView() {

    }

    private fun getDataAccount(userInfo: UserInfo) {
        val docRef = db.collection(userInfo._id!!).document("account")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("ntduc_debug", "DocumentSnapshot data: ${document.data}")
                    val account = document.toObject<UserDB>()
                    if (account != null) {
                        this.account = account
                        updateUI()
                    } else {
                        shortToast("Có lỗi xảy ra, vui lòng thử lại")
                        onBackPressed()
                    }
                } else {
                    shortToast("Có lỗi xảy ra, vui lòng thử lại")
                    onBackPressed()
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "get failed with $e")
                shortToast("Có lỗi xảy ra, vui lòng thử lại")
                onBackPressed()
            }
    }

    private fun updateUI() {
        binding.layoutLoading.root.visibility = View.GONE
    }

    private fun restartApp() {
        val restartIntent = packageManager.getLaunchIntentForPackage(packageName)!!
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(restartIntent)
    }

    companion object{
        const val RESULT_LOGOUT = 100
    }

    private lateinit var binding: ActivityAccountInformationBinding
    private lateinit var viewModel: AccountInformationActivityVM
    private lateinit var db: FirebaseFirestore

    private var mPrefs: Prefs? = null
    private var account: UserDB? = null

    private val changePWLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                mPrefs!!.loadSavedPreferences()
            }
        }
}