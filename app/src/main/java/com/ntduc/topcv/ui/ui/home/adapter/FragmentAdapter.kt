package com.ntduc.topcv.ui.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ntduc.topcv.ui.ui.home.fragment.ApplyJobFragment
import com.ntduc.topcv.ui.ui.home.fragment.FavoriteJobFragment

class FragmentAdapter(
    fa: FragmentActivity,
) : FragmentStateAdapter(fa) {
    private val listFragment = listOf(
        ApplyJobFragment(),
        FavoriteJobFragment()
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