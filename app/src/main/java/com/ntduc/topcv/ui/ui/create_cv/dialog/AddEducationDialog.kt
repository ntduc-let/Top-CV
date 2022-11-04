package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.DialogAddEducationBinding
import com.ntduc.topcv.ui.data.model.Education
import kotlin.math.roundToInt

class AddEducationDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddEducationBinding.inflate(inflater)
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

        binding.textName.setText(education.name)
        binding.textPosition.setText(education.position)
        binding.textStartedAt.setText(education.started_at)
        binding.textEndedAt.setText(education.ended_at)
        binding.textDescriptor.setText(education.descriptor)

        binding.layoutBottom.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.textName.text.trim().isEmpty()) {
                requireContext().shortToast("Tên trường học không được để trống")
                return@setOnClickListener
            }
            education.name = binding.textName.text.trim().toString()
            education.position = binding.textPosition.text.trim().toString()
            education.started_at = binding.textStartedAt.text.trim().toString()
            education.ended_at = binding.textEndedAt.text.trim().toString()
            education.descriptor = binding.textDescriptor.text.trim().toString()

            onSaveListener?.let { it(education) }
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

    private lateinit var binding: DialogAddEducationBinding

    private var onSaveListener: ((Education) -> Unit)? = null

    fun setOnSaveListener(listener: ((Education) -> Unit)) {
        onSaveListener = listener
    }

    private var education: Education = Education()
    fun setEducation(education: Education) {
        this.education = education
    }
}