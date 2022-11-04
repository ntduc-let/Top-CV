package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.DialogAddWorkBinding
import com.ntduc.topcv.ui.data.model.Work
import kotlin.math.roundToInt

class AddWorkDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddWorkBinding.inflate(inflater)
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
                    (requireActivity().displayWidth * 0.9).roundToInt(),
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                val layoutParams = mDialog.window!!.attributes
                layoutParams.gravity = Gravity.CENTER
                mDialog.window!!.attributes = layoutParams
            }
        }

        binding.textName.setText(work.name)
        binding.textPosition.setText(work.position)
        binding.textStartedAt.setText(work.started_at)
        binding.textEndedAt.setText(work.ended_at)
        binding.textDescriptor.setText(work.descriptor)

        binding.layoutBottom.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.textName.text.trim().isEmpty()) {
                requireContext().shortToast("Tên tổ chức không được để trống")
                return@setOnClickListener
            }
            work.name = binding.textName.text.trim().toString()
            work.position = binding.textPosition.text.trim().toString()
            work.started_at = binding.textStartedAt.text.trim().toString()
            work.ended_at = binding.textEndedAt.text.trim().toString()
            work.descriptor = binding.textDescriptor.text.trim().toString()

            onSaveListener?.let { it(work) }
            dismiss()
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

    private lateinit var binding: DialogAddWorkBinding

    private var onSaveListener: ((Work) -> Unit)? = null

    fun setOnSaveListener(listener: ((Work) -> Unit)) {
        onSaveListener = listener
    }

    private var work: Work = Work()
    fun setWork(work: Work) {
        this.work = work
    }
}