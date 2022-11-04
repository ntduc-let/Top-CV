package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CVsDB(
    var listCV: ArrayList<CVDB> = arrayListOf()
) : Parcelable