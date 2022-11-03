package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDB(
    val userInfo: UserInfo? = null,
    var name: String? = null,
    var birthYear: Int? = null,
    var gender: String? = null,
    var height: Int? = null,
    var weight: Int? = null,
    var experience: String? = null,
    var nameOfHighSchool: String? = null,
    var numberHousehold: String? = null,
    var idCCCD: String? = null,
    var hobby: String? = null,
    var personality: String? = null,
    var hometown: String? = null,
    var levelEducational: Int? = null,
    var wish: String? = null,
    var professions: ArrayList<ProfessionDB>? = arrayListOf(),
    var specialConditions: String? = null,
    var workPlace: String? = null,
    var province: String? = null,
    var currentJob: JobDB? = null
) : Parcelable