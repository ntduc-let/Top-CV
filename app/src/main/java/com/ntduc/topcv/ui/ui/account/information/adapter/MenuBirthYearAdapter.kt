package com.ntduc.topcv.ui.ui.account.information.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.ntduc.topcv.databinding.ListItemAccountBinding

class MenuBirthYearAdapter(
    context: Context,
    private var list: List<Int>
) : ArrayAdapter<Int>(context, 0, list), Filterable {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            ListItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val year = getItem(position)
        if (year != null) {
            binding.item.text = year.toString()
        }
        return binding.root
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val year = findYear(constraint.toString())
                    filterResults.values = year
                    filterResults.count = year.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    list = results.values as List<Int>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as Int).toString()
            }
        }
    }

    private fun findYear(str_year: String): ArrayList<Int> {
        val result = arrayListOf<Int>()
        try {
            list.forEach {
                if (it.toString().contains(str_year, true)) {
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            return arrayListOf()
        }
        return result
    }
}