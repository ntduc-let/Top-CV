package com.ntduc.topcv.ui.networking

import com.ntduc.topcv.ui.data.model.UserInfo

interface CallApiListener {
    fun onSuccess(userInfo: UserInfo?)
    fun onError(e: Throwable)
}