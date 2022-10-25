package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobDB(
    var address: String? = null,
    var company: String? = null,
    var profession: String? = null,
    var certificatePhoto: String? = null
) : Parcelable