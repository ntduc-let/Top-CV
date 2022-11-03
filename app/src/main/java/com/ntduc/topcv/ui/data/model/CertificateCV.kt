package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CertificateCV(
    var title: String = "Chứng chỉ",
    var certificates: ArrayList<Certificate> = arrayListOf()
) : Parcelable