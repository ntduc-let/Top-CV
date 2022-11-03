package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Experience(
    var name: String? = null,
    var position: String? = null,
    var started_at: String? = null,
    var ended_at: String? = null,
    var descriptor: String? = null
) : Parcelable