package com.ntduc.topcv.ui.ui.info_job.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ntduc.stringutils.highlight
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.FragmentInfoCompanyBinding
import com.ntduc.topcv.databinding.FragmentInfoJobBinding
import com.ntduc.topcv.ui.ui.info_job.activity.InfoJobActivityVM

class InfoCompanyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoCompanyBinding.inflate(inflater)
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
                binding.txtName.text = it.infoCompanyGlobal.name
                binding.txtAddress.text = "Địa chỉ công ty: ${it.infoCompanyGlobal.address}".highlight(key = "Địa chỉ công ty:", bold = true)
                binding.txtWebsite.text = "Website công ty: ${it.infoCompanyGlobal.website}".highlight(key = "Website công ty:", bold = true)
                binding.txtDescription.text = it.infoCompanyGlobal.description
            }
        }
    }

    private lateinit var binding: FragmentInfoCompanyBinding
    private lateinit var viewModel: InfoJobActivityVM
}