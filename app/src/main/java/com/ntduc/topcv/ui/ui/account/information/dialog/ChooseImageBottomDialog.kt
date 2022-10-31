package com.ntduc.topcv.ui.ui.account.information.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ntduc.topcv.databinding.DialogBottomChooseImageBinding

class ChooseImageBottomDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomChooseImageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTakePhoto.setOnClickListener {
            onTakePhotoListener?.let {
                it()
            }
        }
        binding.txtChooseFromAlbums.setOnClickListener {
            onChooseFromAlbumsListener?.let {
                it()
            }
        }
        binding.txtDelete.setOnClickListener {
            onDeleteListener?.let {
                it()
            }
            dismiss()
        }
        binding.txtClose.setOnClickListener {
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

    private lateinit var binding: DialogBottomChooseImageBinding

    private var onTakePhotoListener: (() -> Unit)? = null

    fun setOnTakePhotoListener(listener: (() -> Unit)) {
        onTakePhotoListener = listener
    }

    private var onChooseFromAlbumsListener: (() -> Unit)? = null

    fun setOnChooseFromAlbumsListener(listener: (() -> Unit)) {
        onChooseFromAlbumsListener = listener
    }

    private var onDeleteListener: (() -> Unit)? = null

    fun setOnDeleteListener(listener: (() -> Unit)) {
        onDeleteListener = listener
    }
}