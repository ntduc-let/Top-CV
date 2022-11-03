package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactInfoCV(
    var title: String = "Thông tin liên hệ",
    var name: String? = null,
    var position: String? = null,
    var phone: String? = null,
    var mail: String? = null,
    var address: String? = null,
    var gender: String? = null,
    var birthDay: String? = null
) : Parcelable