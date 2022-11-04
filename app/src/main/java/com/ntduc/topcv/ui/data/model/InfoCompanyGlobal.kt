package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoCompanyGlobal(
    var src: String = "",
    var name: String = "",
    var address: String = "",
    var website: String = "",
    var description: String = ""
) : Parcelable