package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewAccount(
    val uuid: String?,
    var hash: String?
) : Parcelable