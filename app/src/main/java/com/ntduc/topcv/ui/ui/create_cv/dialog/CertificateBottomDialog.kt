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
import com.ntduc.topcv.databinding.DialogBottomCertificateBinding
import com.ntduc.topcv.ui.data.model.Certificate
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemCertificateAdapter
import com.ntduc.topcv.ui.ui.create_cv.adapter.ItemSkillAdapter

class CertificateBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomCertificateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemCertificateAdapter(requireContext(), ItemCertificateAdapter.MODE_ADD, listCertificate)
        adapter.setOnClickItemListener { certificate ->
            val dialog = AddCertificateDialog()
            dialog.setCertificate(certificate)
            dialog.setOnSaveListener {
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddCertificateDialog")
        }
        binding.rcvList.adapter = adapter
        binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.btnAdd.setOnClickListener {
            val dialog = AddCertificateDialog()
            dialog.setOnSaveListener {
                adapter.list.add(it)
                adapter.reloadData()
            }
            dialog.show(requireActivity().supportFragmentManager, "AddCertificateDialog")
        }

        binding.btnSave.setOnClickListener {
            callback?.onUpdateCertificate(listCertificate)
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

    private lateinit var binding: DialogBottomCertificateBinding
    private lateinit var adapter: ItemCertificateAdapter

    private var listCertificate: ArrayList<Certificate> = ArrayList()
    fun setListCertificate(listCertificate: ArrayList<Certificate>) {
        this.listCertificate = listCertificate
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onUpdateCertificate(list: ArrayList<Certificate>)
    }
}