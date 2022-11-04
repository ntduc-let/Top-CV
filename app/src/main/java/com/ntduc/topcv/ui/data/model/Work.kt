package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Work(
    var name: String = "",
    var position: String = "",
    var started_at: String = "",
    var ended_at: String = "",
    var descriptor: String = ""
) : Parcelable