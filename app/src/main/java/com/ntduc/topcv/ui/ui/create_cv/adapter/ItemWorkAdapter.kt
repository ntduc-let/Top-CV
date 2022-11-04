package com.ntduc.topcv.ui.ui.create_cv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemExperienceShowBinding
import com.ntduc.topcv.databinding.ItemRemoveProfessionBinding
import com.ntduc.topcv.databinding.ItemSkillAddBinding
import com.ntduc.topcv.ui.data.model.Experience
import com.ntduc.topcv.ui.data.model.Work

class ItemWorkAdapter(
    val context: Context,
    val mode: Int,
    var list: ArrayList<Work> = arrayListOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (mode == MODE_SHOW) {
            val binding =
                ItemExperienceShowBinding.inflate(LayoutInflater.from(context), parent, false)
            ShowWorkViewHolder(binding)
        } else {
            val binding =
                ItemSkillAddBinding.inflate(LayoutInflater.from(context), parent, false)
            AddSkillViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is ShowWorkViewHolder) {
            with(holder) {
                binding.txtPosition.text = "${item.position} | ${item.started_at} - ${item.ended_at}"
                binding.txtName.text = item.name
                binding.txtDescriptor.text = item.descriptor
            }
        } else if (holder is AddSkillViewHolder) {
            with(holder) {
                binding.txtName.text = item.name
                binding.btnRemove.setOnClickListener {
                    list.remove(item)
                    reloadData()
                }

                binding.txtName.setOnClickListener {
                    onClickItemListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Work>) {
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

    inner class ShowWorkViewHolder(binding: ItemExperienceShowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemExperienceShowBinding

        init {
            this.binding = binding
        }
    }

    inner class AddSkillViewHolder(binding: ItemSkillAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemSkillAddBinding

        init {
            this.binding = binding
        }
    }

    companion object {
        const val MODE_SHOW = 0
        const val MODE_ADD = 1
    }

    private var onClickItemListener: ((Work) -> Unit)? = null
    fun setOnClickItemListener(listener: (Work) -> Unit) {
        onClickItemListener = listener
    }
}