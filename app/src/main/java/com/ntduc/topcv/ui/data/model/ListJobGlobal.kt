package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListJobGlobal(
    var jobGlobals: ArrayList<JobGlobal> = arrayListOf()
) : Parcelable