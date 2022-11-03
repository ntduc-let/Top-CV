package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkillCV(
    var title: String = "Các kỹ năng",
    var skills: ArrayList<Skill> = arrayListOf()
) : Parcelable