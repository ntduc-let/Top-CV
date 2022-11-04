package com.ntduc.topcv.ui.ui.info_job.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayoutMediator
import com.ntduc.contextutils.inflater
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ActivityInfoJobBinding
import com.ntduc.topcv.ui.data.model.CVDB
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.ui.home.activity.MainActivity
import com.ntduc.topcv.ui.ui.info_job.adapter.FragmentAdapter

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
        ijnitEvent()
    }

    private fun ijnitEvent() {
        binding.toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.btnFavorite.setOnClickListener {

        }

        binding.btnApply.setOnClickListener {

        }
    }

    private fun initData() {
        viewModel.jobGlobal.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.KEY_JOB, JobGlobal::class.java)
        } else {
            intent.getParcelableExtra(MainActivity.KEY_JOB) as JobGlobal?
        }
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[InfoJobActivityVM::class.java]
        viewModel.jobGlobal.observe(this) {
            if (it != null) {
                binding.layoutLoading.root.visibility = View.GONE

                Glide.with(this)
                    .load(it.infoCompanyGlobal.src)
                    .placeholder(R.color.black)
                    .error(R.color.black)
                    .into(binding.title.imgAva)

                binding.title.txtTitle.text = it.infoJobGlobal.position
                binding.title.txtDescription.text = it.infoCompanyGlobal.name
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
}