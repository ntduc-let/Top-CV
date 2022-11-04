package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactInfoCV(
    var title: String = "Thông tin liên hệ",
    var name: String = "Nguyễn Văn A",
    var position: String = "Nhân viên kinh doanh",
    var phone: String = "",
    var email: String = "",
    var address: String = "Số 10, đường 10, TopCV",
    var gender: String = "Name",
    var birthDay: String = "19/05/1992"
) : Parcelable