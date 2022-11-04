package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoJobGlobal(
    var salary: String = "",
    var number: String = "",
    var gender: String = "",
    var experience: String = "",
    var position: String = "",
    var address: String = "",
    var description: String = "",
    var request: String = "",
    var benefit: String = ""
) : Parcelable