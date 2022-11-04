package com.ntduc.topcv.ui.ui.home.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.FragmentJobBinding
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.ui.account.information.activity.AccountInformationActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.GroupJobAdapter
import com.ntduc.topcv.ui.ui.home.model.GroupJob
import com.ntduc.topcv.ui.ui.home.model.Job
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity
import com.ntduc.topcv.ui.ui.search.activity.SearchJobActivity
import com.ntduc.topcv.ui.utils.Prefs

class JobFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        binding.layoutToolbarJob.imgAva.setOnClickListener {
            if (mPrefs!!.isLogin){
                accountLauncher.launch(Intent(requireContext(), AccountInformationActivity::class.java))
            }else{
                loginLauncher.launch(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        binding.layoutSearchJob.root.setOnClickListener {
            if (mPrefs!!.isLogin){
                startActivity(Intent(requireContext(), SearchJobActivity::class.java))
            }else{
                requireContext().shortToast("Vui lòng đăng nhập để sử dụng tính năng này")
            }
        }
    }

    private fun initData() {
        mPrefs = Prefs(requireContext())

        viewModel = ViewModelProvider(requireActivity())[MainActivityVM::class.java]
        viewModel.userDB.observe(viewLifecycleOwner) {
            binding.layoutLoading.root.visibility = View.GONE
            if (it != null) {
                binding.layoutToolbarJob.txtAccount.text = "Chào mừng bạn quay trở lại, ${it.name}"
                Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/topcv-androidnc.appspot.com/o/${it.userInfo!!._id}%2Faccount%2Favatar%2Favatar.jpg?alt=media")
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.color.black)
                    .error(R.drawable.ic_ava_48dp)
                    .into(binding.layoutToolbarJob.imgAva)
            } else {
                binding.layoutToolbarJob.txtAccount.text = "Chào bạn"
                binding.layoutToolbarJob.imgAva.setImageResource(R.drawable.ic_ava_48dp)
            }
        }


        val list = loadGroupJob()
        adapter.updateListGroupJob(list)
    }

    private fun initView() {
        adapter = GroupJobAdapter(requireContext())
        binding.rcvJob.adapter = adapter
        binding.rcvJob.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun loadGroupJob(): ArrayList<GroupJob> {
        val groupJobs = arrayListOf<GroupJob>()
        for (i in 0..5) {
            val groupJob = GroupJob()
            groupJob.title = "Việc làm tốt nhất"
            groupJob.isMore = true

            val jobs = arrayListOf<Job>()
            for (j in 0..5) {
                val job = Job()
                job.name = "Nhân viên văn phòng"
                job.description = "CÔNG TY TNHH SẢN XUẤT - THƯƠNG MẠI MINH..."
                job.salary = "Trên 7.5 triệu"
                job.location = "Quận 8, Hồ Chí Minh"
                job.date = "Còn 22 ngày để ứng tuyển"
                jobs.add(job)
            }

            groupJob.jobs = jobs
            groupJobs.add(groupJob)
        }
        return groupJobs
    }

    private lateinit var binding: FragmentJobBinding
    private lateinit var adapter: GroupJobAdapter
    private lateinit var viewModel: MainActivityVM

    private var mPrefs: Prefs? = null

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB, UserDB::class.java)
                } else {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB) as UserDB?
                }
                if (userDB != null) {
                    binding.layoutLoading.root.visibility = View.VISIBLE
                    viewModel.userDB.value = userDB
                }
                mPrefs!!.loadSavedPreferences()
            }
        }

    private val accountLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AccountInformationActivity.RESULT_LOGOUT) {
                binding.layoutLoading.root.visibility = View.VISIBLE
                viewModel.userDB.value = null
                mPrefs!!.loadSavedPreferences()
            }else if (it.resultCode == AccountInformationActivity.RESULT_UPDATE){
                binding.layoutLoading.root.visibility = View.VISIBLE
                viewModel.userDB.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB, UserDB::class.java)
                } else {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB) as UserDB?
                }
            }
        }
}