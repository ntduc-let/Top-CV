package com.ntduc.topcv.ui.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ActivitySplashBinding
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity
import com.ntduc.topcv.ui.utils.Prefs

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
        initEvent()
    }

    private fun initEvent() {
        if (mPrefs!!.email != null && mPrefs!!.password != null) {
            val account = Account(account = mPrefs!!.email, hash = mPrefs!!.password)

            viewModel.loginAccount(
                account = account,
                callApiListener = object : CallApiListener {
                    override fun onSuccess(userInfo: UserInfo?) {
                        if (userInfo != null) {
                            getDataAccount(userInfo)
                        } else {
                            mPrefs!!.updateEmail(null)
                            mPrefs!!.updatePassword(null)
                            startHome(null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        mPrefs!!.updateEmail(null)
                        mPrefs!!.updatePassword(null)
                        startHome(null)
                    }
                })
        } else {
            startHome(null)
        }
    }

    private fun getDataAccount(userInfo: UserInfo) {
        val docRef = db.collection(userInfo._id!!).document("account")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("ntduc_debug", "DocumentSnapshot data: ${document.data}")
                    val account = document.toObject<UserDB>()
                    if (account != null) {
                        startHome(account)
                    } else {
                        recreateDataAccount(userInfo)
                    }
                } else {
                    startHome(null)
                }
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "get failed with $e")
                startHome(null)
            }
    }

    private fun recreateDataAccount(userInfo: UserInfo) {
        val account = UserDB(userInfo = userInfo, name = userInfo.account)

        db.collection(userInfo._id!!).document("account")
            .set(account)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                startHome(account)
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                startHome(null)
            }
    }

    private fun startHome(account: UserDB?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.KEY_USER_DB, account)
        startActivity(intent)
        finish()
    }

    private fun initData() {
        mPrefs = Prefs(this)
        db = Firebase.firestore
        viewModel = ViewModelProvider(this)[SplashActivityVM::class.java]
    }

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashActivityVM
    private lateinit var db: FirebaseFirestore

    private var mPrefs: Prefs? = null
}