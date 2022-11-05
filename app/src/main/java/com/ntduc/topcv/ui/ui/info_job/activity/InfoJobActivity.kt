package com.ntduc.topcv.ui.ui.info_job.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.contextutils.inflater
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityInfoJobBinding
import com.ntduc.topcv.ui.data.model.*
import com.ntduc.topcv.ui.ui.dialog.LoadingDialog
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.info_job.adapter.FragmentAdapter
import com.ntduc.topcv.ui.ui.info_job.dialog.ChooseCVBottomDialog
import com.ntduc.topcv.ui.utils.Prefs

class InfoJobActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoJobBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        binding.toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.btnFavorite.setOnClickListener {
            if (mPrefs!!.isLogin) {
                updateFavorite()
            } else {
                shortToast("Vui lòng đăng nhập để thực hiện tính năng này")
            }
        }

        binding.btnApply.setOnClickListener {
            if (mPrefs!!.isLogin) {
                if (listCV.isEmpty()){
                    shortToast("Vui lòng tạo CV để thực hiện ứng tuyển")
                }else{
                    val dialog = ChooseCVBottomDialog()
                    dialog.setListCV(listCV)
                    dialog.setOnClickItemListener {
                        applyJob()
                        dialog.dismiss()
                    }
                    dialog.show(supportFragmentManager, "ChooseCVBottomDialog")
                }
            } else {
                shortToast("Vui lòng đăng nhập để thực hiện tính năng này")
            }
        }
    }

    private fun applyJob() {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")

        listApply.add(viewModel.jobGlobal.value!!)

        val listJobGlobal = ListJobGlobal()
        listJobGlobal.jobGlobals = listApply

        db.collection(MainActivity.userDB!!.userInfo!!._id!!).document("job_apply")
            .set(listJobGlobal)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                loadingDialog?.dismiss()

                binding.btnApply.text = "Đã ứng tuyển"
                binding.btnApply.isEnabled = false
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun updateFavorite() {
        if (binding.toolbar.btnFavorite.isActivated) {
            listFav.remove(viewModel.jobGlobal.value!!)
        } else {
            listFav.add(viewModel.jobGlobal.value!!)
        }

        loadingDialog = LoadingDialog()
        loadingDialog!!.show(supportFragmentManager, "LoadingDialog")
        val listJobGlobal = ListJobGlobal()
        listJobGlobal.jobGlobals = listFav

        db.collection(MainActivity.userDB!!.userInfo!!._id!!).document("job_favorite")
            .set(listJobGlobal)
            .addOnSuccessListener {
                Log.d("ntduc_debug", "DocumentSnapshot successfully written!")
                loadingDialog?.dismiss()

                binding.toolbar.btnFavorite.isActivated = !binding.toolbar.btnFavorite.isActivated
            }
            .addOnFailureListener { e ->
                Log.d("ntduc_debug", "Error writing document: $e")
                loadingDialog?.dismiss()

                shortToast("Có lỗi xảy ra. Vui lòng thử lại")
            }
    }

    private fun initData() {
        viewModel.jobGlobal.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.KEY_JOB, JobGlobal::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.KEY_JOB) as JobGlobal?
        }

        getDataCV(MainActivity.userDB!!)
    }

    private fun getDataCV(userDB: UserDB) {
        val docRef = db.collection(userDB.userInfo!!._id!!).document("cv")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cVsDB = document.toObject<CVsDB>()
                    if (cVsDB != null) {
                        listCV = cVsDB.listCV
                    }
                }
            }
    }


    private fun initView() {
        db = Firebase.firestore
        mPrefs = Prefs(this)

        viewModel = ViewModelProvider(this)[InfoJobActivityVM::class.java]
        viewModel.jobGlobal.observe(this) {
            if (it != null) {
                Glide.with(this)
                    .load(it.infoCompanyGlobal.src)
                    .placeholder(R.color.black)
                    .error(R.color.black)
                    .into(binding.title.imgAva)

                binding.title.txtTitle.text = it.infoJobGlobal.position
                binding.title.txtDescription.text = it.infoCompanyGlobal.name

                val docRef =
                    db.collection(MainActivity.userDB!!.userInfo!!._id!!).document("job_favorite")
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val list = document.toObject<ListJobGlobal>()
                            if (list != null) {
                                listFav = list.jobGlobals
                                list.jobGlobals.forEach { job ->
                                    if (job.id == it.id) {
                                        binding.toolbar.btnFavorite.isActivated = true
                                        return@forEach
                                    }
                                }
                            }
                        }
                        val applyRef =
                            db.collection(MainActivity.userDB!!.userInfo!!._id!!)
                                .document("job_apply")
                        applyRef.get()
                            .addOnSuccessListener { apply ->
                                if (apply != null) {
                                    val applys = apply.toObject<ListJobGlobal>()
                                    if (applys != null) {
                                        listApply = applys.jobGlobals
                                        applys.jobGlobals.forEach { job ->
                                            if (job.id == it.id) {
                                                binding.btnApply.text = "Đã ứng tuyển"
                                                binding.btnApply.isEnabled = false
                                                return@forEach
                                            }
                                        }
                                    }
                                }
                                binding.layoutLoading.root.visibility = View.GONE
                            }
                            .addOnFailureListener {
                                shortToast("Có lỗi xảy ra, vui lòng thử lại")
                            }
                    }
                    .addOnFailureListener {
                        shortToast("Có lỗi xảy ra, vui lòng thử lại")
                    }
            }
        }
        adapter = FragmentAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Thông tin"
                1 -> tab.text = "Công ty"
            }
        }.attach()
    }

    private lateinit var binding: ActivityInfoJobBinding
    private lateinit var viewModel: InfoJobActivityVM
    private lateinit var adapter: FragmentAdapter
    private lateinit var db: FirebaseFirestore

    private var listCV: ArrayList<CVDB> = arrayListOf()
    private var listFav: ArrayList<JobGlobal> = arrayListOf()
    private var listApply: ArrayList<JobGlobal> = arrayListOf()
    private var mPrefs: Prefs? = null
    private var loadingDialog: LoadingDialog? = null

}