package com.ntduc.topcv.ui.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ntduc.topcv.databinding.FragmentCvBinding
import com.ntduc.topcv.ui.ui.create_cv.activity.CreateCVActivity
import com.ntduc.topcv.ui.ui.home.activity.MainActivityVM
import com.ntduc.topcv.ui.ui.home.adapter.CVAdapter
import com.ntduc.topcv.ui.utils.Prefs

class CVFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCvBinding.inflate(inflater)
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
        binding.layoutNoCv.btnCreate.setOnClickListener {
            startActivity(Intent(requireContext(), CreateCVActivity::class.java))
        }

        binding.layoutListCv.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(), CreateCVActivity::class.java))
        }
    }

    private fun initData() {
        mPrefs = Prefs(requireContext())

        viewModel = ViewModelProvider(requireActivity())[MainActivityVM::class.java]
        viewModel.userDB.observe(viewLifecycleOwner) {
            binding.layoutLoading.root.visibility = View.GONE
//            if (it != null) {
//                binding.layoutToolbarJob.txtAccount.text = "Chào mừng bạn quay trở lại, ${it.name}"
//                Glide.with(this)
//                    .load("https://firebasestorage.googleapis.com/v0/b/topcv-androidnc.appspot.com/o/${it.userInfo!!._id}%2Faccount%2Favatar%2Favatar.jpg?alt=media")
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .placeholder(R.color.black)
//                    .error(R.drawable.ic_ava_48dp)
//                    .into(binding.layoutToolbarJob.imgAva)
//            } else {
//                binding.layoutToolbarJob.txtAccount.text = "Chào bạn"
//                binding.layoutToolbarJob.imgAva.setImageResource(R.drawable.ic_ava_48dp)
//            }
        }
    }

    private fun initView() {
        adapter = CVAdapter(requireContext())
        binding.layoutListCv.rcvList.adapter = adapter
        binding.layoutListCv.rcvList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private lateinit var binding: FragmentCvBinding
    private lateinit var adapter: CVAdapter
    private lateinit var viewModel: MainActivityVM

    private var mPrefs: Prefs? = null
}