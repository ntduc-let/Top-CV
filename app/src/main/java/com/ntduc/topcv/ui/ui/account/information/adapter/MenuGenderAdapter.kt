package com.ntduc.topcv.ui.ui.account.information.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.ntduc.topcv.databinding.ListItemAccountBinding

class MenuGenderAdapter(
    context: Context,
    private var list: List<String>
) : ArrayAdapter<String>(context, 0, list), Filterable {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ListItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val gender = getItem(position)
        if (gender != null) {
            binding.item.text = gender
        }
        return binding.root
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val year = findGender(constraint.toString())
                    filterResults.values = year
                    filterResults.count = year.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    list = results.values as List<String>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as String).toString()
            }
        }
    }

    private fun findGender(gender: String): ArrayList<String> {
        val result = arrayListOf<String>()
        try {
            list.forEach {
                if (it.contains(gender, true)) {
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            return arrayListOf()
        }
        return result
    }
}