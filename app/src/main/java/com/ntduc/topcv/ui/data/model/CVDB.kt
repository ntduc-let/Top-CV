package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CVDB(
    var id: String? = null,
    var title: String? = null,
    var update_at: String? = null,
    var contactInfoCV: ContactInfoCV? = null,
    var careerGoalsCV: CareerGoalsCV? = null,
    var skillCV: SkillCV? = null,
    var experienceCV: ExperienceCV? = null,
    var educationCV: EducationCV? = null,
    var workCV: WorkCV? = null,
    var certificateCV: CertificateCV? = null
) : Parcelable