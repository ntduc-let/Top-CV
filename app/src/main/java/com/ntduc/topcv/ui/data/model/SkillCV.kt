package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkillCV(
    var title: String = "Các kỹ năng",
    var skills: ArrayList<Skill> = getDefaultSkills()
) : Parcelable

fun getDefaultSkills(): ArrayList<Skill> {
    val result: ArrayList<Skill> = arrayListOf()
    val skill = Skill(name = "Tiếng Anh", descriptor = "Khả năng giao diếp Tiếng Anh trôi chảy")
    result.add(skill)
    return result
}
