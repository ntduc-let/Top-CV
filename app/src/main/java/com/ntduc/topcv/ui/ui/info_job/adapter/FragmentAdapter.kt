package com.ntduc.topcv.ui.ui.info_job.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ntduc.topcv.ui.ui.info_job.fragment.InfoCompanyFragment
import com.ntduc.topcv.ui.ui.info_job.fragment.InfoJobFragment

class FragmentAdapter(
    fa: FragmentActivity,
) : FragmentStateAdapter(fa) {
    private val listFragment = listOf(
        InfoJobFragment(),
        InfoCompanyFragment()
    )

    override fun createFragment(position: Int): Fragment {
        if (position < listFragment.size) {
            return listFragment[position]
        }
        return listFragment[0]
    }

    override fun getItemCount(): Int {
        return listFragment.size
    }
}