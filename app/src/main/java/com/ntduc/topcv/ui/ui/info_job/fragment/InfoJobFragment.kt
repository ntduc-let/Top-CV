package com.ntduc.topcv.ui.ui.info_job.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ntduc.stringutils.highlight
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.FragmentInfoJobBinding
import com.ntduc.topcv.ui.ui.info_job.activity.InfoJobActivityVM

class InfoJobFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoJobBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        viewModel = ViewModelProvider(requireActivity())[InfoJobActivityVM::class.java]

        viewModel.jobGlobal.observe(viewLifecycleOwner){
            if (it != null){
                binding.txtSalary.text = "Mức lương: ${it.infoJobGlobal.salary}".highlight(key = "Mức lương:", bold = true)
                binding.txtNumber.text = "Số lượng cần tuyển: ${it.infoJobGlobal.number}".highlight(key = "Số lượng cần tuyển:", bold = true)
                binding.txtGender.text = "Giới tính: ${it.infoJobGlobal.gender}".highlight(key = "Giới tính:", bold = true)
                binding.txtExperience.text = "Kinh nghiệm: ${it.infoJobGlobal.experience}".highlight(key = "Kinh nghiệm:", bold = true)
                binding.txtPosition.text = "Chức vụ: ${it.infoJobGlobal.position}".highlight(key = "Chức vụ:", bold = true)
                binding.txtAddress.text = "Địa chỉ: ${it.infoJobGlobal.address}".highlight(key = "Địa chỉ:", bold = true)
                binding.txtDescription.text = it.infoJobGlobal.description
                binding.txtRequest.text = it.infoJobGlobal.request
                binding.txtBenefit.text = it.infoJobGlobal.benefit
            }
        }
    }

    private lateinit var binding: FragmentInfoJobBinding
    private lateinit var viewModel: InfoJobActivityVM
}