package com.ntduc.topcv.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class Prefs(mContext: Context) {
    private val mSharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(mContext)

    var isLogin: Boolean = false
    var email: String? = null
    var password: String? = null

    init {
        loadSavedPreferences()
    }

    fun loadSavedPreferences() {
        isLogin = mSharedPreferences.getBoolean(PREF_KEY_IS_LOGIN, false)
        email = mSharedPreferences.getString(PREF_KEY_EMAIL, null)
        password = mSharedPreferences.getString(PREF_KEY_PASSWORD, null)
    }

    fun saveLogin(isLogin: Boolean, email: String? = null, password: String? = null){
        updateIsLogin(isLogin)
        updateEmail(email)
        updatePassword(password)
    }

    fun updateIsLogin(isLogin: Boolean) {
        this.isLogin = isLogin
        val sharedPreferencesEditor = mSharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(PREF_KEY_IS_LOGIN, isLogin)
        sharedPreferencesEditor.apply()
    }

    fun updateEmail(email: String?) {
        this.email = email
        val sharedPreferencesEditor = mSharedPreferences.edit()
        sharedPreferencesEditor.putString(PREF_KEY_EMAIL, email)
        sharedPreferencesEditor.apply()
    }

    fun updatePassword(password: String?) {
        this.password = password
        val sharedPreferencesEditor = mSharedPreferences.edit()
        sharedPreferencesEditor.putString(PREF_KEY_PASSWORD, password)
        sharedPreferencesEditor.apply()
    }

    companion object {
        private const val PREF_KEY_EMAIL = "email"
        private const val PREF_KEY_PASSWORD = "password"
        private const val PREF_KEY_IS_LOGIN = "isLogin"
    }
}