package com.ntduc.topcv.ui.ui.create_cv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemExperienceShowBinding
import com.ntduc.topcv.databinding.ItemRemoveProfessionBinding
import com.ntduc.topcv.databinding.ItemSkillShowBinding
import com.ntduc.topcv.ui.data.model.Experience
import com.ntduc.topcv.ui.data.model.Skill

class ItemExperienceAdapter(
    val context: Context,
    val mode: Int,
    var list: ArrayList<Experience> = arrayListOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (mode == MODE_SHOW) {
            val binding =
                ItemExperienceShowBinding.inflate(LayoutInflater.from(context), parent, false)
            ShowExperienceViewHolder(binding)
        } else {
            val binding =
                ItemRemoveProfessionBinding.inflate(LayoutInflater.from(context), parent, false)
            RemoveProfessionViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is ShowExperienceViewHolder) {
            with(holder) {
                binding.txtPosition.text = "${item.position} | ${item.started_at} - ${item.ended_at}"
                binding.txtName.text = item.name
                binding.txtDescriptor.text = item.descriptor
            }
        } else if (holder is RemoveProfessionViewHolder) {
//            with(holder) {
//                binding.txtTitle.text = item.professionDB.name
//                binding.btnRemove.setOnClickListener {
//                    item.isSelected = false
//                    list.remove(item)
//                    reloadData()
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Experience>) {
        list = arrayListOf()
        newList.forEach {
            list.add(it)
        }
        reloadData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reloadData() {
        notifyDataSetChanged()
    }

    inner class ShowExperienceViewHolder(binding: ItemExperienceShowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemExperienceShowBinding

        init {
            this.binding = binding
        }
    }

    inner class RemoveProfessionViewHolder(binding: ItemRemoveProfessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemRemoveProfessionBinding

        init {
            this.binding = binding
        }
    }

    companion object {
        const val MODE_SHOW = 0
        const val MODE_ADD = 1
    }

//    private var onClickItemListener: ((Profession) -> Unit)? = null
//    fun setOnClickItemListener(listener: (Profession) -> Unit) {
//        onClickItemListener = listener
//    }
}