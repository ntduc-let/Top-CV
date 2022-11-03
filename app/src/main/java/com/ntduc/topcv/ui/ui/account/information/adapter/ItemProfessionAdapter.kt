package com.ntduc.topcv.ui.ui.account.information.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.toastutils.shortToast
import com.ntduc.topcv.databinding.ItemRemoveProfessionBinding
import com.ntduc.topcv.databinding.ItemSelectProfessionBinding
import com.ntduc.topcv.ui.data.model.Profession

class ItemProfessionAdapter(
    val context: Context,
    val mode: Int,
    var list: ArrayList<Profession> = arrayListOf()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        list.forEach {
            if (it.isSelected) numberSelect++
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (mode == MODE_SELECT) {
            val binding =
                ItemSelectProfessionBinding.inflate(LayoutInflater.from(context), parent, false)
            SelectProfessionViewHolder(binding)
        } else {
            val binding =
                ItemRemoveProfessionBinding.inflate(LayoutInflater.from(context), parent, false)
            RemoveProfessionViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is SelectProfessionViewHolder) {
            with(holder) {
                binding.txtTitle.text = item.professionDB.name
                binding.btnSelect.isActivated = item.isSelected

                binding.root.setOnClickListener {
                    if (!item.isSelected) {
                        if (numberSelect >= 6) {
                            context.shortToast("Tối đa 6 lựa chọn")
                        } else {
                            numberSelect++
                            item.isSelected = true
                            binding.btnSelect.isActivated = true
                            onClickItemListener?.let {
                                it(item)
                            }
                        }
                    } else {
                        numberSelect--
                        item.isSelected = false
                        binding.btnSelect.isActivated = false
                        onClickItemListener?.let {
                            it(item)
                        }
                    }
                }
            }
        } else if (holder is RemoveProfessionViewHolder) {
            with(holder) {
                binding.txtTitle.text = item.professionDB.name
                binding.btnRemove.setOnClickListener {
                    item.isSelected = false
                    list.remove(item)
                    reloadData()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Profession>) {
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

    inner class SelectProfessionViewHolder(binding: ItemSelectProfessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemSelectProfessionBinding

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
        const val MODE_SELECT = 0
        const val MODE_REMOVE = 1
    }

    private var numberSelect: Int = 0

    private var onClickItemListener: ((Profession) -> Unit)? = null
    fun setOnClickItemListener(listener: (Profession) -> Unit) {
        onClickItemListener = listener
    }
}