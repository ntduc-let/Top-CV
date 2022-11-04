package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JobGlobal(
    var id: String = "",
    var infoJobGlobal: InfoJobGlobal = InfoJobGlobal(),
    var infoCompanyGlobal: InfoCompanyGlobal = InfoCompanyGlobal(),
    var listCV: ArrayList<CVDB> = arrayListOf()
) : Parcelable