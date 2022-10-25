package com.ntduc.topcv.ui.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.topcv.databinding.DialogLoadingBinding

class LoadingDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mDialog = dialog
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(false)
            if (mDialog.window != null) {
                mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mDialog.window!!.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                val layoutParams = mDialog.window!!.attributes
                layoutParams.gravity = Gravity.CENTER
                mDialog.window!!.attributes = layoutParams
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

    private lateinit var binding: DialogLoadingBinding
}