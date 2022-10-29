package com.ntduc.topcv.ui.ui.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.UserInfo
import com.ntduc.topcv.ui.data.repository.TopCVRepository
import com.ntduc.topcv.ui.networking.CallApiListener
import kotlinx.coroutines.*

class SplashActivityVM : ViewModel() {
    companion object {
        private const val TAG: String = "xLoginActivityVM"
    }

    private var callApiListener: CallApiListener? = null

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d(TAG, "Throw Exception: $exception")
        callApiListener?.onError(exception)
    }

    fun loginAccount(account: Account, callApiListener: CallApiListener) {
        this.callApiListener = callApiListener

        viewModelScope.launch(handler) {
            try {
                withTimeout(15000) {
                    val userInfo = login(account)
                    this@SplashActivityVM.callApiListener?.onSuccess(userInfo)
                }
            } catch (e: TimeoutCancellationException) {
                Log.d(TAG, "Error : time out request")
                callApiListener.onError(e)
            }
        }
    }

    private suspend fun login(account: Account): UserInfo? {
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