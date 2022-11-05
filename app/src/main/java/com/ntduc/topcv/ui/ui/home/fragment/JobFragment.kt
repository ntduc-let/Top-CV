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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.FragmentJobBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.UserDB
import com.ntduc.topcv.ui.ui.account.information.activity.AccountInformationActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.GroupJobAdapter
import com.ntduc.topcv.ui.ui.home.model.GroupJob
import com.ntduc.topcv.ui.ui.home.model.Job
import com.ntduc.topcv.ui.ui.info_job.activity.InfoJobActivity
import com.ntduc.topcv.ui.ui.login.activity.LoginActivity
import com.ntduc.topcv.ui.ui.search.activity.SearchJobActivity
import com.ntduc.topcv.ui.utils.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            if (mPrefs!!.isLogin) {
                accountLauncher.launch(
                    Intent(
                        requireContext(),
                        AccountInformationActivity::class.java
                    )
                )
            } else {
                loginLauncher.launch(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        binding.layoutSearchJob.root.setOnClickListener {
            if (mPrefs!!.isLogin) {
                startActivity(Intent(requireContext(), SearchJobActivity::class.java))
            } else {
                requireContext().shortToast("Vui lòng đăng nhập để sử dụng tính năng này")
            }
        }

        adapter.setOnClickItemListener {
            if (mPrefs!!.isLogin) {
                val intent = Intent(requireContext(), InfoJobActivity::class.java)
                intent.putExtra(MainActivity.KEY_JOB, it)
                startActivity(intent)
            } else {
                requireContext().shortToast("Vui lòng đăng nhập để sử dụng tính năng này")
            }
        }
    }

    private fun initData() {
        db = Firebase.firestore
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
                CVFragment.listCV = arrayListOf()
            }
        }

        db.collection("top_cv_global")
            .get()
            .addOnSuccessListener { result ->
                listJob = arrayListOf()
                lifecycleScope.launch(Dispatchers.IO) {
                    for (document in result) {
                        val job = document.toObject<JobGlobal>()
                        if (job.id.isNotEmpty()) {
                            listJob.add(job)
                        }
                    }
                    val listGroup: ArrayList<GroupJob> = arrayListOf()
                    val groupJob1 = GroupJob()
                    groupJob1.title = "Việc làm tốt nhất"
                    groupJob1.isMore = false
                    for (i in 0..19) {
                        groupJob1.jobs.add(listJob[i])
                    }
                    listGroup.add(groupJob1)

                    val groupJob2 = GroupJob()
                    groupJob2.title = "Việc làm hấp dẫn"
                    groupJob2.isMore = false
                    for (i in 20..39) {
                        groupJob2.jobs.add(listJob[i])
                    }
                    listGroup.add(groupJob2)

                    val groupJob3 = GroupJob()
                    groupJob3.title = "Việc làm lương cao"
                    groupJob3.isMore = false
                    for (i in 40..59) {
                        groupJob3.jobs.add(listJob[i])
                    }
                    listGroup.add(groupJob3)

                    withContext(Dispatchers.Main) {
                        binding.layoutLoading.root.visibility = View.GONE

                        adapter.updateListGroupJob(listGroup)
                    }
                }
            }
            .addOnFailureListener {
                binding.layoutLoading.root.visibility = View.GONE
            }
    }

    private fun initView() {
        adapter = GroupJobAdapter(requireContext())
        binding.rcvJob.adapter = adapter
        binding.rcvJob.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private lateinit var binding: FragmentJobBinding
    private lateinit var adapter: GroupJobAdapter
    private lateinit var viewModel: MainActivityVM
    private lateinit var db: FirebaseFirestore
    private var listJob: ArrayList<JobGlobal> = arrayListOf()

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
                MainActivity.userDB = userDB
                mPrefs!!.loadSavedPreferences()
            }
        }

    private val accountLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AccountInformationActivity.RESULT_LOGOUT) {
                binding.layoutLoading.root.visibility = View.VISIBLE
                MainActivity.userDB = null
                viewModel.userDB.value = null
                mPrefs!!.loadSavedPreferences()
            } else if (it.resultCode == AccountInformationActivity.RESULT_UPDATE) {
                binding.layoutLoading.root.visibility = View.VISIBLE
                MainActivity.userDB = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB, UserDB::class.java)
                } else {
                    it.data?.getParcelableExtra(MainActivity.KEY_USER_DB) as UserDB?
                }
                viewModel.userDB.value = MainActivity.userDB
            }
        }
}