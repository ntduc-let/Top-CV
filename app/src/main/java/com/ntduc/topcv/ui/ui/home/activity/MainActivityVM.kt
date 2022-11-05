package com.ntduc.topcv.ui.ui.home.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.UserDB

class MainActivityVM : ViewModel() {
    companion object {
        private const val TAG: String = "xMainActivityVM"
    }

    val userDB: MutableLiveData<UserDB?> = MutableLiveData(null)
    val favoriteJob: MutableLiveData<ArrayList<JobGlobal>?> = MutableLiveData(null)
    val applyJob: MutableLiveData<ArrayList<JobGlobal>?> = MutableLiveData(null)
}