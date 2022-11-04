package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CertificateCV(
    var title: String = "Chứng chỉ",
    var certificates: ArrayList<Certificate> = getDefaultCertificate()
) : Parcelable

fun getDefaultCertificate(): ArrayList<Certificate> {
    val result: ArrayList<Certificate> = arrayListOf()
    val certificate = Certificate(name = "Giải nhất Tài năng Top Cv", time = "2013")
    result.add(certificate)
    return result
}
