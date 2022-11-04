package com.ntduc.topcv.ui.data.model

import android.os.Parcelable
import com.ntduc.datetimeutils.currentCalendar
import kotlinx.parcelize.Parcelize

@Parcelize
data class CVDB(
    var id: String = currentCalendar.timeInMillis.toString(),
    var title: String = "Untitled CV",
    var update_at: String = currentCalendar.timeInMillis.toString(),
    var contactInfoCV: ContactInfoCV = ContactInfoCV(),
    var careerGoalsCV: CareerGoalsCV = CareerGoalsCV(),
    var skillCV: SkillCV = SkillCV(),
    var experienceCV: ExperienceCV = ExperienceCV(),
    var educationCV: EducationCV = EducationCV(),
    var workCV: WorkCV = WorkCV(),
    var certificateCV: CertificateCV = CertificateCV()
) : Parcelable