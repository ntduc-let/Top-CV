package com.ntduc.topcv.ui.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemGroupJobBinding
import com.ntduc.topcv.databinding.ItemJobBinding
import com.ntduc.topcv.ui.ui.home.model.GroupJob
import com.ntduc.topcv.ui.ui.home.model.Job

class JobAdapter(
    val context: Context,
    var listJob: List<Job> = listOf()
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemJobBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(context), parent, false)
        return JobViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val item = listJob[position]

//        holder.binding.imgAva.setImageURI()
        holder.binding.txtTitle.text = item.name
        holder.binding.txtDescription.text = item.description
        holder.binding.txtSalary.text = item.salary
        holder.binding.txtLocation.text = item.location
        holder.binding.txtDate.text = item.date
    }

    override fun getItemCount(): Int {
        return listJob.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListJob(list: List<Job>) {
        listJob = list
        notifyDataSetChanged()
    }
}