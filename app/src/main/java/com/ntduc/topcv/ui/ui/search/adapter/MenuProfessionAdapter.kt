package com.ntduc.topcv.ui.ui.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.ntduc.topcv.databinding.ListItemAccountBinding
import com.ntduc.topcv.ui.data.model.ProfessionDB

class MenuProfessionAdapter(
    context: Context,
    private var list: List<ProfessionDB>
) : ArrayAdapter<ProfessionDB>(context, 0, list), Filterable {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ListItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val gender = getItem(position)
        if (gender != null) {
            binding.item.text = gender.name
        }
        return binding.root
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val year = findProfession(constraint.toString())
                    filterResults.values = year
                    filterResults.count = year.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    list = results.values as List<ProfessionDB>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as ProfessionDB).name.toString()
            }
        }
    }

    private fun findProfession(gender: String): ArrayList<ProfessionDB> {
        val result = arrayListOf<ProfessionDB>()
        try {
            list.forEach {
                if (it.name!!.contains(gender, true)) {
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            return arrayListOf()
        }
        return result
    }
}