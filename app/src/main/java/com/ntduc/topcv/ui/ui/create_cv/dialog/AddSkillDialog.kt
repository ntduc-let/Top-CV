package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.DialogAddSkillBinding
import com.ntduc.topcv.ui.data.model.Skill
import kotlin.math.roundToInt

class AddSkillDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddSkillBinding.inflate(inflater)
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

        binding.textName.setText(skill.name)
        binding.textDescriptor.setText(skill.descriptor)

        binding.layoutBottom.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.textName.text.trim().isEmpty()){
                requireContext().shortToast("Nhóm kỹ năng không được để trống")
                return@setOnClickListener
            }
            skill.name = binding.textName.text.trim().toString()
            skill.descriptor = binding.textDescriptor.text.trim().toString()

            onSaveListener?.let { it(skill) }
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

    private lateinit var binding: DialogAddSkillBinding

    private var onSaveListener: ((Skill) -> Unit)? = null

    fun setOnSaveListener(listener: ((Skill) -> Unit)) {
        onSaveListener = listener
    }

    private var skill: Skill = Skill()
    fun setSkill(skill: Skill) {
        this.skill = skill
    }
}