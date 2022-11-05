package com.ntduc.topcv.ui.ui.info_job.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ntduc.topcv.databinding.DialogBottomChooseCvBinding
import com.ntduc.topcv.ui.data.model.CVDB
import com.ntduc.topcv.ui.data.model.Profession
import com.ntduc.topcv.ui.ui.account.information.adapter.ItemProfessionAdapter
import com.ntduc.topcv.ui.ui.home.adapter.CVAdapter

class ChooseCVBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomChooseCvBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CVAdapter(requireContext(), listCV, CVAdapter.CHOOSE)
        adapter.setOnClickItemListener { cv ->
            onClickItemListener?.let {
                it(cv)
            }
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
                this.setCancelable(true)
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

    private lateinit var binding: DialogBottomChooseCvBinding
    private lateinit var adapter: CVAdapter

    private var listCV: ArrayList<CVDB> = ArrayList()
    fun setListCV(listCV: ArrayList<CVDB>) {
        this.listCV = listCV
    }

    private var onClickItemListener: ((CVDB) -> Unit)? = null
    fun setOnClickItemListener(listener: (CVDB) -> Unit) {
        onClickItemListener = listener
    }
}