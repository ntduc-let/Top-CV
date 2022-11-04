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
import com.ntduc.topcv.databinding.DialogBottomWorkBinding
import com.ntduc.topcv.ui.data.model.Work
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemEducationAdapter
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemWorkAdapter

class WorkBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomWorkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemWorkAdapter(requireContext(), ItemWorkAdapter.MODE_ADD, listWork)
        adapter.setOnClickItemListener { work ->
            val dialog = AddWorkDialog()
            dialog.setWork(work)
            dialog.setOnSaveListener {
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddWorkDialog")
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.btnAdd.setOnClickListener {
            val dialog = AddWorkDialog()
            dialog.setOnSaveListener {
                adapter.list.add(it)
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddWorkDialog")
        }

        binding.btnSave.setOnClickListener {
            callback?.onUpdateWork(listWork)
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

    private lateinit var binding: DialogBottomWorkBinding
    private lateinit var adapter: ItemWorkAdapter

    private var listWork: ArrayList<Work> = ArrayList()
    fun setListWork(listWork: ArrayList<Work>) {
        this.listWork = listWork
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onUpdateWork(list: ArrayList<Work>)
    }
}