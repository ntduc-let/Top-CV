package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDB(
    val userInfo: UserInfo? = null,
    var name: String? = null,
    var age: Int? = null,
    var birthYear: Int? = null,
    var gender: String? = null,
    var height: String? = null,
    var weight: String? = null,
    var experience: String? = null,
    var nameOfHighSchool: String? = null,
    var numberHousehold: String? = null,
    var idCCCD: String? = null,
    var hobby: String? = null,
    var personality: String? = null,
    var hometown: String? = null,
    var levelEducational: String? = null,
    var wish: String? = null,
    var profession: String? = null,
    var specialConditions: String? = null,
    var salary: String? = null,
    var region: String? = null,
    var province: String? = null,
    var currentJob: JobDB? = null
) : Parcelable