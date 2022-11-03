package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CareerGoalsCV(
    var title: String = "Mục tiêu nghề nghiệp",
    var content: String? = null
) : Parcelable