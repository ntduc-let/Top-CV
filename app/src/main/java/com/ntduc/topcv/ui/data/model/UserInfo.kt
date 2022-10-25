package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val _id: String? = null,
    val account: String? = null,
    val created_at: String? = null,
    var updated_at: String? = null
) : Parcelable