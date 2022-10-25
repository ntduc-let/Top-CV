package com.ntduc.topcv.ui.ui.home.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntduc.topcv.ui.data.model.UserDB

class MainActivityVM : ViewModel() {
    companion object {
        private const val TAG: String = "xMainActivityVM"
    }

    val userDB: MutableLiveData<UserDB?> = MutableLiveData(null)
}