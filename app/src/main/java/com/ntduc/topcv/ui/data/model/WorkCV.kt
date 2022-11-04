package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkCV(
    var title: String = "Hoạt động",
    var works: ArrayList<Work> = getDefaultWork()
) : Parcelable

fun getDefaultWork(): ArrayList<Work> {
    val result: ArrayList<Work> = arrayListOf()
    val work = Work(
        name = "Nhóm tình nguyện TOP CV",
        position = "Tình nguyện viên",
        started_at = "10/2013",
        ended_at = "08/2014",
        descriptor = "Tập hợp các món quà và phân phát tới người vô gia cư"
    )
    result.add(work)
    return result
}
