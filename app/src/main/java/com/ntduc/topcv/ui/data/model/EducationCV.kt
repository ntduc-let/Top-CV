package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EducationCV(
    var title: String = "Học vấn",
    var educations: ArrayList<Education> = arrayListOf()
) : Parcelable