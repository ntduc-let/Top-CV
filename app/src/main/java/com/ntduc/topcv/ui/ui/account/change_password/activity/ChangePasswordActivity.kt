package com.ntduc.topcv.ui.ui.account.change_password.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ActivityChangePasswordBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.NewAccount
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.networking.ChangePasswordApiListener
import com.ntduc.topcv.ui.ui.account.information.activity.AccountInformationActivity
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.utils.Prefs

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.layoutBottom.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (checkInformation()) {
                val account = Account(
                    account = mPrefs!!.email,
                    hash = binding.edtCurrentPassword.text.toString()
                )
                checkPassword(account)
            }
        }
    }

    private fun initView() {
        binding.edtEmail.setText(mPrefs!!.email)
    }

    private fun initData() {
        mPrefs = Prefs(this)
        viewModel = ViewModelProvider(this)[ChangePasswordActivityVM::class.java]
        db = Firebase.firestore
    }

    private fun checkPassword(account: Account) {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        viewModel.checkPassword(
            account = account,
            callApiListener = object : CallApiListener {
                override fun onSuccess(userInfo: UserInfo?) {
                    if (userInfo != null) {
                        val newAccount = NewAccount(
                            uuid = userInfo._id,
                            hash = binding.edtNewPassword.text.toString()
                        )
                        viewModel.changePassword(newAccount = newAccount, changePasswordApiListener = object : ChangePasswordApiListener{
                            override fun onSuccess(success: Boolean) {
                                loadingDialog?.dismiss()
                                if (success){
                                    shortToast("?????i m???t kh???u th??nh c??ng")

                                    mPrefs!!.updatePassword(binding.edtNewPassword.text.toString())
                                    val intent = Intent(this@ChangePasswordActivity, AccountInformationActivity::class.java)
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }else{
                                    shortToast("?????i m???t kh???u th???t b???i")
                                }
                            }

                            override fun onError(e: Throwable) {
                                loadingDialog?.dismiss()
                                shortToast("H??? th???ng kh??ng ph???n h???i ho???c kh??ng c?? k???t n???i Internet")
                            }
                        })
                    } else {
                        loadingDialog?.dismiss()
                        shortToast("M???t kh???u c?? kh??ng ch??nh x??c")
                    }
                }

                override fun onError(e: Throwable) {
                    loadingDialog?.dismiss()
                    shortToast("H??? th???ng kh??ng ph???n h???i ho???c kh??ng c?? k???t n???i Internet")
                }
            })
    }


    private fun checkInformation(): Boolean {
        if (binding.edtCurrentPassword.text.isEmpty()
            || binding.edtCurrentPassword.text.length !in 6..25
            || binding.edtNewPassword.text.isEmpty()
            || binding.edtNewPassword.text.length !in 6..25
            || binding.edtNewPassword2.text.isEmpty()
            || binding.edtNewPassword2.text.length !in 6..25
            || binding.edtNewPassword.text.toString() != binding.edtNewPassword2.text.toString()
        ) {
            if (binding.edtCurrentPassword.text.isEmpty()) {
                binding.notificationCurrentPassword.text = "M???t kh???u kh??ng ???????c ????? tr???ng"
                binding.notificationCurrentPassword.visibility = View.VISIBLE
            } else if (binding.edtCurrentPassword.text.length !in 6..25) {
                binding.notificationCurrentPassword.text = "M???t kh???u h???p l??? t??? 6 - 25 k?? t???"
                binding.notificationCurrentPassword.visibility = View.VISIBLE
            } else {
                binding.notificationCurrentPassword.visibility = View.GONE
            }

            if (binding.edtNewPassword.text.isEmpty()) {
                binding.notificationNewPassword.text = "M???t kh???u kh??ng ???????c ????? tr???ng"
                binding.notificationNewPassword.visibility = View.VISIBLE
            } else if (binding.edtNewPassword.text.length !in 6..25) {
                binding.notificationNewPassword.text = "M???t kh???u h???p l??? t??? 6 - 25 k?? t???"
                binding.notificationNewPassword.visibility = View.VISIBLE
            } else {
                binding.notificationNewPassword.visibility = View.GONE
            }
            if (binding.edtNewPassword2.text.isEmpty()) {
                binding.notificationNewPassword2.text = "Vui l??ng x??c nh???n l???i m???t kh???u"
                binding.notificationNewPassword2.visibility = View.VISIBLE
            } else if (binding.edtNewPassword.text.toString() != binding.edtNewPassword2.text.toString()) {
                binding.notificationNewPassword2.text = "M???t kh???u kh??ng kh???p"
                binding.notificationNewPassword2.visibility = View.VISIBLE
            } else {
                binding.notificationNewPassword2.visibility = View.GONE
            }
            return false
        }

        binding.notificationCurrentPassword.visibility = View.GONE
        binding.notificationNewPassword.visibility = View.GONE
        binding.notificationNewPassword2.visibility = View.GONE
        return true
    }

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: ChangePasswordActivityVM
    private lateinit var db: FirebaseFirestore

    private var mPrefs: Prefs? = null
    private var loadingDialog: LoadingDialog? = null
}