package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Certificate(
    var name: String = "",
    var time: String = ""
) : Parcelable