package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkCV(
    var title: String = "Hoạt động",
    var works: ArrayList<Work> = arrayListOf()
) : Parcelable