package com.ntduc.topcv.ui.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ntduc.topcv.R
import com.ntduc.topcv.databinding.ItemGroupJobBinding
import com.ntduc.topcv.databinding.ItemJobBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.ui.home.model.GroupJob
import com.ntduc.topcv.ui.ui.home.model.Job

class JobAdapter(
    val context: Context,
    var listJob: List<JobGlobal> = listOf()
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

        Glide.with(context)
            .load(item.infoCompanyGlobal.src)
            .placeholder(R.color.black)
            .error(R.color.black)
            .into(holder.binding.imgAva)

        holder.binding.txtTitle.text = item.infoJobGlobal.position
        holder.binding.txtDescription.text = item.infoCompanyGlobal.name
        holder.binding.txtSalary.text = item.infoJobGlobal.salary
        holder.binding.txtLocation.text = item.infoJobGlobal.address
        holder.binding.root.setOnClickListener {
            onClickItemListener?.let { it(item) }
        }
    }

    override fun getItemCount(): Int {
        return listJob.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListJob(list: List<JobGlobal>) {
        listJob = list
        notifyDataSetChanged()
    }

    private var onClickItemListener: ((JobGlobal) -> Unit)? = null
    fun setOnClickItemListener(listener: (JobGlobal) -> Unit) {
        onClickItemListener = listener
    }
}