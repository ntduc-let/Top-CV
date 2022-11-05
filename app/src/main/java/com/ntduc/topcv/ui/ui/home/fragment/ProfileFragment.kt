package com.ntduc.topcv.ui.ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.FragmentProfileBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.ListJobGlobal
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.FragmentAdapter
import com.ntduc.topcv.ui.utils.Prefs

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initView()
        initData()
    }

    private fun initData() {
        if (!mPrefs!!.isLogin) return

        val docRef =
            db.collection(MainActivity.userDB!!.userInfo!!._id!!).document("job_favorite")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val list = document.toObject<ListJobGlobal>()
                    if (list != null) {
                        viewModel.favoriteJob.value = list.jobGlobals
                    }
                }
            }
            .addOnFailureListener {
                requireActivity().shortToast("Có lỗi xảy ra, vui lòng thử lại")
            }

        val applyRef =
            db.collection(MainActivity.userDB!!.userInfo!!._id!!)
                .document("job_apply")
        applyRef.get()
            .addOnSuccessListener { apply ->
                if (apply != null) {
                    val applys = apply.toObject<ListJobGlobal>()
                    if (applys != null) {
                        viewModel.applyJob.value = applys.jobGlobals
                    }
                }
            }
            .addOnFailureListener {
                requireActivity().shortToast("Có lỗi xảy ra, vui lòng thử lại")
            }
    }

    private fun initView() {
        db = Firebase.firestore
        mPrefs = Prefs(requireContext())

        viewModel = ViewModelProvider(requireActivity())[MainActivityVM::class.java]

        adapter = FragmentAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Đã ứng tuyển"
                1 -> tab.text = "Đã lưu"
            }
        }.attach()
    }

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: MainActivityVM
    private lateinit var adapter: FragmentAdapter
    private lateinit var db: FirebaseFirestore

    private var listFav: ArrayList<JobGlobal> = arrayListOf()
    private var listApply: ArrayList<JobGlobal> = arrayListOf()
    private var mPrefs: Prefs? = null
}