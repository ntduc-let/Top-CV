package com.ntduc.topcv.ui.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemCvBinding
import com.ntduc.topcv.ui.data.model.CVDB

class CVAdapter(
    val context: Context,
    var listCV: List<CVDB> = listOf()
) : RecyclerView.Adapter<CVAdapter.CVViewHolder>() {

    inner class CVViewHolder(binding: ItemCvBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemCvBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CVViewHolder {
        val binding = ItemCvBinding.inflate(LayoutInflater.from(context), parent, false)
        return CVViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CVViewHolder, position: Int) {
        val item = listCV[position]

        holder.binding.txtTitle.text = item.title
        holder.binding.txtUpdate.text = item.update_at
    }

    override fun getItemCount(): Int {
        return listCV.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<CVDB>) {
        listCV = list
        notifyDataSetChanged()
    }
}