package com.ntduc.topcv.ui.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemChooseCvBinding
import com.ntduc.topcv.databinding.ItemCvBinding
import com.ntduc.topcv.ui.data.model.CVDB
import com.ntduc.topcv.ui.data.model.Skill

class CVAdapter(
    val context: Context,
    var listCV: List<CVDB> = listOf(),
    val mode: Int = DEFAULT
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CVViewHolder(binding: ItemCvBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemCvBinding

        init {
            this.binding = binding
        }
    }

    inner class ChooseCVViewHolder(binding: ItemChooseCvBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemChooseCvBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (mode == DEFAULT){
            val binding = ItemCvBinding.inflate(LayoutInflater.from(context), parent, false)
            CVViewHolder(binding)
        }else{
            val binding = ItemChooseCvBinding.inflate(LayoutInflater.from(context), parent, false)
            ChooseCVViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listCV[position]

        if (holder is CVViewHolder){
            holder.binding.txtTitle.text = item.title
            holder.binding.txtUpdate.text = "Cập nhật lần cuối: ${item.update_at}"

            holder.binding.root.setOnClickListener {
                onClickItemListener?.let {
                    it(item)
                }
            }
            holder.binding.btnDelete.setOnClickListener {
                onDeleteItemListener?.let {
                    it(item)
                }
            }
        }else if (holder is ChooseCVViewHolder){
            holder.binding.txtTitle.text = item.title
            holder.binding.txtUpdate.text = "Cập nhật lần cuối: ${item.update_at}"

            holder.binding.root.setOnClickListener {
                onClickItemListener?.let {
                    it(item)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return listCV.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<CVDB>) {
        listCV = list
        notifyDataSetChanged()
    }

    private var onClickItemListener: ((CVDB) -> Unit)? = null
    fun setOnClickItemListener(listener: (CVDB) -> Unit) {
        onClickItemListener = listener
    }

    private var onDeleteItemListener: ((CVDB) -> Unit)? = null
    fun setOnDeleteItemListener(listener: (CVDB) -> Unit) {
        onDeleteItemListener = listener
    }

    companion object{
        const val DEFAULT = 0
        const val CHOOSE = 1
    }
}