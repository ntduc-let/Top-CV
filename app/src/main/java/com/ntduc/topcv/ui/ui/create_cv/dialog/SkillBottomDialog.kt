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
import com.ntduc.topcv.databinding.DialogBottomSkillBinding
import com.ntduc.topcv.ui.data.model.Skill
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemSkillAdapter

class SkillBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSkillBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemSkillAdapter(requireContext(), ItemSkillAdapter.MODE_ADD, listSkill)
        adapter.setOnClickItemListener { skill ->
            val dialog = AddSkillDialog()
            dialog.setSkill(skill)
            dialog.setOnSaveListener {
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddSkillDialog")
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.btnAdd.setOnClickListener {
            val dialog = AddSkillDialog()
            dialog.setOnSaveListener {
                adapter.list.add(it)
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddSkillDialog")
        }

        binding.btnSave.setOnClickListener {
            callback?.onUpdateSkill(listSkill)
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

    private lateinit var binding: DialogBottomSkillBinding
    private lateinit var adapter: ItemSkillAdapter

    private var listSkill: ArrayList<Skill> = ArrayList()
    fun setListSkill(listSkill: ArrayList<Skill>) {
        this.listSkill = listSkill
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onUpdateSkill(list: ArrayList<Skill>)
    }
}