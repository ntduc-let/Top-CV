package com.ntduc.topcv.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.FragmentJobBinding
import com.ntduc.topcv.ui.home.adapter.GroupJobAdapter
import com.ntduc.topcv.ui.home.model.GroupJob
import com.ntduc.topcv.ui.home.model.Job
import com.ntduc.topcv.ui.login.activity.LoginActivity

class JobFragment : Fragment() {
    private lateinit var binding: FragmentJobBinding
    private lateinit var adapter: GroupJobAdapter

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
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
    }

    private fun initData() {
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
}