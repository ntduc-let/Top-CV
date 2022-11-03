package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExperienceCV(
    var title: String = "Kinh nghiệm làm việc",
    var experiences: ArrayList<Experience> = arrayListOf()
) : Parcelable