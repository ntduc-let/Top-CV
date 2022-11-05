package com.ntduc.topcv.ui.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.topcv.databinding.ItemGroupJobBinding
import com.ntduc.topcv.ui.data.model.JobGlobal
import com.ntduc.topcv.ui.ui.home.model.GroupJob

class GroupJobAdapter(
    val context: Context,
    var listGroupJob: List<GroupJob> = listOf()
) : RecyclerView.Adapter<GroupJobAdapter.GroupJobViewHolder>() {

    inner class GroupJobViewHolder(binding: ItemGroupJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: ItemGroupJobBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupJobViewHolder {
        val binding = ItemGroupJobBinding.inflate(LayoutInflater.from(context), parent, false)
        return GroupJobViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupJobViewHolder, position: Int) {
        val item = listGroupJob[position]

        holder.binding.txtTitle.text = item.title
        holder.binding.txtMore.visibility = if (item.isMore) View.VISIBLE else View.INVISIBLE
        val adapter = JobAdapter(context, item.jobs)
        holder.binding.rcvList.adapter = adapter
        holder.binding.rcvList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter.setOnClickItemListener { job ->
            onClickItemListener?.let { it(job) }
        }
    }

    override fun getItemCount(): Int {
        return listGroupJob.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListGroupJob(list: List<GroupJob>) {
        listGroupJob = list
        notifyDataSetChanged()
    }

    private var onClickItemListener: ((JobGlobal) -> Unit)? = null
    fun setOnClickItemListener(listener: (JobGlobal) -> Unit) {
        onClickItemListener = listener
    }
}