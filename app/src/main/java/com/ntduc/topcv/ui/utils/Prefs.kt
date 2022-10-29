package com.ntduc.topcv.ui.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class Prefs(mContext: Context) {
    private val mSharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(mContext)

    var email: String? = null
    var password: String? = null

    init {
        loadSavedPreferences()
    }

    private fun loadSavedPreferences() {
        email = mSharedPreferences.getString(PREF_KEY_EMAIL, null)
        password = mSharedPreferences.getString(PREF_KEY_PASSWORD, null)
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
    }
}