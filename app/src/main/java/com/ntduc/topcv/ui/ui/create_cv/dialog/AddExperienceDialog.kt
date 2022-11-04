package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.DialogAddExperienceBinding
import com.ntduc.topcv.ui.data.model.Experience
import kotlin.math.roundToInt

class AddExperienceDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddExperienceBinding.inflate(inflater)
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

        binding.textName.setText(experience.name)
        binding.textPosition.setText(experience.position)
        binding.textStartedAt.setText(experience.started_at)
        binding.textEndedAt.setText(experience.ended_at)
        binding.textDescriptor.setText(experience.descriptor)

        binding.layoutBottom.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.textName.text.trim().isEmpty()) {
                requireContext().shortToast("Tên công ty không được để trống")
                return@setOnClickListener
            }
            experience.name = binding.textName.text.trim().toString()
            experience.position = binding.textPosition.text.trim().toString()
            experience.started_at = binding.textStartedAt.text.trim().toString()
            experience.ended_at = binding.textEndedAt.text.trim().toString()
            experience.descriptor = binding.textDescriptor.text.trim().toString()

            onSaveListener?.let { it(experience) }
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

    private lateinit var binding: DialogAddExperienceBinding

    private var onSaveListener: ((Experience) -> Unit)? = null

    fun setOnSaveListener(listener: ((Experience) -> Unit)) {
        onSaveListener = listener
    }

    private var experience: Experience = Experience()
    fun setExperience(experience: Experience) {
        this.experience = experience
    }
}