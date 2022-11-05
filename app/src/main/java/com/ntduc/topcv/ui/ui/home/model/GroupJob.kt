package com.ntduc.topcv.ui.ui.home.model

import com.ntduc.topcv.ui.data.model.JobGlobal

open class GroupJob(
    var title: String = "null",
    var isMore: Boolean = false,
    var jobs: ArrayList<JobGlobal> = arrayListOf()
)