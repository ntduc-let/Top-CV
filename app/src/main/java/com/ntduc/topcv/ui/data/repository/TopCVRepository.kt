package com.ntduc.topcv.ui.data.repository

import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.networking.RetrofitService

object TopCVRepository {
    private const val TAG: String = "xTopCVRepository"

    private val topCVApi = RetrofitService.topCVApi
    suspend fun createAccount(account: Account) = topCVApi.createAccount(account = account)
    suspend fun loginAccount(account: Account) = topCVApi.loginAccount(account = account)
}
