package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ntduc.topcv.databinding.DialogBottomEducationBinding
import com.ntduc.topcv.ui.data.model.Education
import com.ntduc.topcv.ui.data.model.Experience
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemEducationAdapter
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemExperienceAdapter
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemSkillAdapter

class EducationBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomEducationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemEducationAdapter(requireContext(), ItemEducationAdapter.MODE_ADD, listEducation)
        adapter.setOnClickItemListener { education ->
            val dialog = AddEducationDialog()
            dialog.setEducation(education)
            dialog.setOnSaveListener {
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddEducationDialog")
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.btnAdd.setOnClickListener {
            val dialog = AddEducationDialog()
            dialog.setOnSaveListener {
                adapter.list.add(it)
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddEducationDialog")
        }

        binding.btnSave.setOnClickListener {
            callback?.onUpdateEducation(listEducation)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
                this.setCancelable(false)
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded) {
            return
        }
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        if (isAdded) {
            try {
                super.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private lateinit var binding: DialogBottomEducationBinding
    private lateinit var adapter: ItemEducationAdapter

    private var listEducation: ArrayList<Education> = ArrayList()
    fun setListEducation(listEducation: ArrayList<Education>) {
        this.listEducation = listEducation
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onUpdateEducation(list: ArrayList<Education>)
    }
}