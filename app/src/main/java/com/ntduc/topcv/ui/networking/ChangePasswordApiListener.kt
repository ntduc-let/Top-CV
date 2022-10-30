package com.ntduc.topcv.ui.networking

interface ChangePasswordApiListener {
    fun onSuccess(success: Boolean)
    fun onError(e: Throwable)
}