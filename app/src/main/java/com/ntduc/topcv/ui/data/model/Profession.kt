package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profession(
    val professionDB: ProfessionDB,
    var isSelected: Boolean = false,
) : Parcelable