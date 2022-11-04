package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EducationCV(
    var title: String = "Học vấn",
    var educations: ArrayList<Education> = getDefaultEducation()
) : Parcelable

fun getDefaultEducation(): ArrayList<Education> {
    val result: ArrayList<Education> = arrayListOf()
    val education = Education(
        name = "Đại học TOP CV",
        position = "Quản trị Doanh nghiệp",
        started_at = "10/2010",
        ended_at = "05/2014",
        descriptor = "Tốt nghiệp loại Giỏi, điểm trung bình 8.0"
    )
    result.add(education)
    return result
}
