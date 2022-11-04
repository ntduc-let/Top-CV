package com.ntduc.topcv.ui.ui.info_job.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntduc.topcv.ui.data.model.JobGlobal

class InfoJobActivityVM : ViewModel() {
    companion object {
        private const val TAG: String = "xInfoJobActivityVM"
    }

    val jobGlobal: MutableLiveData<JobGlobal?> = MutableLiveData(null)
}