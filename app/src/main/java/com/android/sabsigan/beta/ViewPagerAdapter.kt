package com.android.sabsigan.beta

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa:FragmentActivity): FragmentStateAdapter(fa) {
    private var mFragmentList = mutableListOf<Fragment>()
    override fun getItemCount(): Int = mFragmentList.size

    override fun createFragment(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    public fun setFragmentList( fragmentList: List<Fragment>) {
        mFragmentList = fragmentList.toMutableList()
    }

    public fun addFragment( fragment: Fragment) {
        mFragmentList.add(fragment)
    }
}