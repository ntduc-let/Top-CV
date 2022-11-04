package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExperienceCV(
    var title: String = "Kinh nghiệm làm việc",
    var experiences: ArrayList<Experience> = getDefaultExperience()
) : Parcelable

fun getDefaultExperience(): ArrayList<Experience> {
    val result: ArrayList<Experience> = arrayListOf()
    val experiences = Experience(
        name = "Công ty TOP CV",
        position = "Nhân viên bán hàng",
        started_at = "03/2015",
        ended_at = "Hiện tại",
        descriptor = "Hỗ trợ viết bài quảng cáo sản phẩm qua kênh facebook, các forum,..."
    )
    result.add(experiences)
    return result
}
