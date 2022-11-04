package com.ntduc.topcv.ui.ui.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ItemJobSearchBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.data.model.ProfessionDB

class JobSearchAdapter(
    val context: Context,
    var listJob: List<JobGlobal> = listOf()
) : RecyclerView.Adapter<JobSearchAdapter.JobViewHolder>() {

    inner class JobViewHolder(binding: ItemJobSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemJobSearchBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return JobViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val item = listJob[position]

        Glide.with(context)
            .load(item.infoCompanyGlobal.src)
            .placeholder(R.color.black)
            .error(R.color.black)
            .into(holder.binding.imgAva)

        holder.binding.txtTitle.text = item.infoJobGlobal.position
        holder.binding.txtDescription.text = item.infoCompanyGlobal.name
        holder.binding.txtSalary.text = item.infoJobGlobal.salary
        holder.binding.txtLocation.text = item.infoJobGlobal.address
    }

    override fun getItemCount(): Int {
        return listJob.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<JobGlobal>) {
        listJob = list
        notifyDataSetChanged()
    }
}