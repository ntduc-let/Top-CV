package com.ntduc.topcv.ui.ui.search.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ntduc.contextutils.displayWidth
import com.ntduc.topcv.databinding.DialogFilterBinding
import com.ntduc.topcv.ui.data.model.ProfessionDB
import com.ntduc.topcv.ui.ui.account.information.adapter.MenuGenderAdapter
import com.ntduc.topcv.ui.ui.search.adapter.MenuProfessionAdapter
import kotlin.math.roundToInt

class FilterDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mDialog = dialog
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(true)
            if (mDialog.window != null) {
                mDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                mDialog.window!!.setLayout(
                    (requireActivity().displayWidth * 0.8).roundToInt(),
                    WindowManager.LayoutParams.MATCH_PARENT
                )

                val layoutParams = mDialog.window!!.attributes
                layoutParams.gravity = Gravity.END
                mDialog.window!!.attributes = layoutParams
            }
        }

        val menuLocationAdapter = MenuGenderAdapter(requireContext(), dataLocation)
        binding.textLocation.setAdapter(menuLocationAdapter)
        binding.textLocation.setText(dataLocation[positionLocation])
        binding.textLocation.setOnItemClickListener { _, _, position, _ ->
            positionLocation = position
        }

        val menuProfessionAdapter = MenuProfessionAdapter(requireContext(), dataProfession)
        binding.textProfession.setAdapter(menuProfessionAdapter)
        binding.textProfession.setText(dataProfession[positionProfession].name)
        binding.textProfession.setOnItemClickListener { _, _, position, _ ->
            positionProfession = position
        }

        val menuSalaryAdapter = MenuGenderAdapter(requireContext(), dataSalary)
        binding.textSalary.setAdapter(menuSalaryAdapter)
        binding.textSalary.setText(dataSalary[positionSalary])
        binding.textSalary.setOnItemClickListener { _, _, position, _ ->
            positionSalary = position
        }

        val menuExperienceAdapter = MenuGenderAdapter(requireContext(), dataExperience)
        binding.textExperience.setAdapter(menuExperienceAdapter)
        binding.textExperience.setText(dataExperience[positionExperience])
        binding.textExperience.setOnItemClickListener { _, _, position, _ ->
            positionExperience = position
        }

        binding.btnApply.setOnClickListener {
            onApplyListener?.let {
                it(positionLocation, positionProfession, positionSalary, positionExperience)
            }
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

    private lateinit var binding: DialogFilterBinding

    private var onApplyListener: ((Int, Int, Int, Int) -> Unit)? = null

    fun setOnApplyListener(listener: ((Int, Int, Int, Int) -> Unit)) {
        onApplyListener = listener
    }

    private var dataLocation = arrayListOf<String>()
    private var positionLocation = 0
    fun setDataLocation(dataLocation: ArrayList<String>, position: Int) {
        this.dataLocation = dataLocation
        this.positionLocation = position
    }

    private var dataProfession = arrayListOf<ProfessionDB>()
    private var positionProfession = 0
    fun setDataProfession(dataProfession: ArrayList<ProfessionDB>, position: Int) {
        this.dataProfession = dataProfession
        this.positionProfession = position
    }

    private var dataSalary = arrayListOf<String>()
    private var positionSalary = 0
    fun setDataSalary(dataSalary: ArrayList<String>, position: Int) {
        this.dataSalary = dataSalary
        this.positionSalary = position
    }

    private var dataExperience = arrayListOf<String>()
    private var positionExperience = 0
    fun setDataExperience(dataExperience: ArrayList<String>, position: Int) {
        this.dataExperience = dataExperience
        this.positionExperience = position
    }
}