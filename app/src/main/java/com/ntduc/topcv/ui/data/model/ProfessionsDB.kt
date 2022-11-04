package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfessionsDB(
    var professions: ArrayList<ProfessionDB> = arrayListOf()
) : Parcelable