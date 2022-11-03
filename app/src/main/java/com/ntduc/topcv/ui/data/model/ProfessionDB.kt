package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfessionDB(
    val _id: String? = null,
    var name: String? = null,
) : Parcelable