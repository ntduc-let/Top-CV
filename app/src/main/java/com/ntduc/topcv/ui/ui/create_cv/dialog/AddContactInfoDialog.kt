package com.ntduc.topcv.ui.ui.create_cv.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.DialogAddContactInfoBinding
import com.ntduc.topcv.ui.data.model.ContactInfoCV
import kotlin.math.roundToInt

class AddContactInfoDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddContactInfoBinding.inflate(inflater)
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

        binding.textName.setText(contactInfoCV.name)
        binding.textPosition.setText(contactInfoCV.position)
        binding.textPhone.setText(contactInfoCV.phone)
        binding.textEmail.setText(contactInfoCV.email)
        binding.textAddress.setText(contactInfoCV.address)
        binding.textGender.setText(contactInfoCV.gender)
        binding.textBirthDay.setText(contactInfoCV.birthDay)

        binding.layoutBottom.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.layoutBottom.btnSave.setOnClickListener {
            if (binding.textName.text.trim().isEmpty()) {
                requireContext().shortToast("Họ tên không được để trống")
                return@setOnClickListener
            }
            contactInfoCV.name = binding.textName.text.trim().toString()
            contactInfoCV.position = binding.textPosition.text.trim().toString()
            contactInfoCV.phone = binding.textPhone.text.trim().toString()
            contactInfoCV.email = binding.textEmail.text.trim().toString()
            contactInfoCV.address = binding.textAddress.text.trim().toString()
            contactInfoCV.gender = binding.textGender.text.trim().toString()
            contactInfoCV.birthDay = binding.textBirthDay.text.trim().toString()

            onSaveListener?.let { it(contactInfoCV) }
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

    private lateinit var binding: DialogAddContactInfoBinding

    private var onSaveListener: ((ContactInfoCV) -> Unit)? = null

    fun setOnSaveListener(listener: ((ContactInfoCV) -> Unit)) {
        onSaveListener = listener
    }

    private var contactInfoCV: ContactInfoCV = ContactInfoCV()
    fun setContactInfoCV(contactInfoCV: ContactInfoCV) {
        this.contactInfoCV = contactInfoCV
    }
}