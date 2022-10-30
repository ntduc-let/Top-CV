package com.ntduc.topcv.ui.networking

import com.ntduc.topcv.ui.data.model.Account
import com.ntduc.topcv.ui.data.model.NewAccount
import com.ntduc.topcv.ui.data.model.ResponseAccount
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TopCVApi {
    @POST("v1/user/create")
    suspend fun createAccount(
        @Body account: Account): Response<ResponseAccount>

    @POST("v1/user/hash")
    suspend fun loginAccount(
        @Body account: Account): Response<ResponseAccount>

    @POST("v1/user/password")
    suspend fun changePassword(
        @Body newAccount: NewAccount): Response<ResponseAccount>
}