package com.ntduc.topcv.ui.ui.account.information.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ntduc.topcv.databinding.DialogBottomChooseProfessionBinding
import com.ntduc.topcv.ui.data.model.Profession
import com.ntduc.topcv.ui.ui.account.information.adapter.ItemProfessionAdapter

class ChooseProfessionBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomChooseProfessionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemProfessionAdapter(requireContext(), ItemProfessionAdapter.MODE_SELECT, listProfession)
        adapter.setOnClickItemListener { profession ->
            onClickItemListener?.let {
                it(profession)
            }
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.textSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (arg0 == "") {
                    adapter.updateData(listProfession)
                } else {
                    filter(arg0.toString())
                }
            }
        })
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

    private fun filter(text: String) {
        val result = listProfession.filter {
            it.professionDB.name!!.contains(text, true)
        }
        if (result.isEmpty()) {
            binding.txtNoItem.visibility = View.VISIBLE
            binding.rcvList.visibility = View.INVISIBLE
        } else {
            binding.txtNoItem.visibility = View.INVISIBLE
            binding.rcvList.visibility = View.VISIBLE
            adapter.updateData(result)
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

    private lateinit var binding: DialogBottomChooseProfessionBinding
    private lateinit var adapter: ItemProfessionAdapter

    private var listProfession: ArrayList<Profession> = ArrayList()
    fun setListProfession(listProfession: ArrayList<Profession>) {
        this.listProfession = listProfession
    }

    private var onClickItemListener: ((Profession) -> Unit)? = null
    fun setOnClickItemListener(listener: (Profession) -> Unit) {
        onClickItemListener = listener
    }
}