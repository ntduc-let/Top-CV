package com.ntduc.topcv.ui.ui.account.change_password.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.NewAccount
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.data.repository.TopCVRepository
import com.ntduc.topcv.ui.networking.CallApiListener
import com.ntduc.topcv.ui.networking.ChangePasswordApiListener
import kotlinx.coroutines.*

class ChangePasswordActivityVM : ViewModel() {
    companion object {
        private const val TAG: String = "xChangePasswordActivityVM"
    }

    private var callApiListener: CallApiListener? = null

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "Throw Exception: $exception")
        callApiListener?.onError(exception)
    }

    private var changePasswordApiListener: ChangePasswordApiListener? = null

    private val handlerChangePW = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "Throw Exception: $exception")
        changePasswordApiListener?.onError(exception)
    }

    fun changePassword(newAccount: NewAccount, changePasswordApiListener: ChangePasswordApiListener) {
        this.changePasswordApiListener = changePasswordApiListener

        viewModelScope.launch(handlerChangePW) {
            try {
                withTimeout(15000) {
                    val error = change(newAccount)
                    this@ChangePasswordActivityVM.changePasswordApiListener?.onSuccess(!error)
                }
            } catch (e: TimeoutCancellationException) {
                Log.d(TAG, "Error : time out request")
                changePasswordApiListener.onError(e)
            }
        }
    }

    fun checkPassword(account: Account, callApiListener: CallApiListener) {
        this.callApiListener = callApiListener

        viewModelScope.launch(handler) {
            try {
                withTimeout(15000) {
                    val userInfo = check(account)
                    this@ChangePasswordActivityVM.callApiListener?.onSuccess(userInfo)
                }
            } catch (e: TimeoutCancellationException) {
                Log.d(TAG, "Error : time out request")
                callApiListener.onError(e)
            }
        }
    }

    private suspend fun change(newAccount: NewAccount): Boolean {
        val result = withContext(Dispatchers.IO) {
            TopCVRepository.changePassword(newAccount = newAccount)
        }

        return if (result.isSuccessful) {
            val response = result.body()
            response?.error ?: false
        } else {
            false
        }
    }

    private suspend fun check(account: Account): UserInfo? {
        val result = withContext(Dispatchers.IO) {
            TopCVRepository.loginAccount(account = account)
        }

        if (result.isSuccessful) {
            val response = result.body()
            if (response != null && response.error == true) return null
            else if (response != null && response.error == false && response.message == "success") return response.data?.userInfo
        } else {
            return null
        }
        return null
    }
}