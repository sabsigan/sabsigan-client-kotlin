package com.android.sabsigan.Wifi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa:FragmentActivity): FragmentStateAdapter(fa) {
    private var mFragmentList = mutableListOf<Fragment>()
    override fun getItemCount(): Int = mFragmentList.size

    override fun createFragment(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    fun setFragmentList(fragmentList: List<Fragment>) {
        mFragmentList = fragmentList.toMutableList()
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
        notifyItemChanged(mFragmentList.size - 1)
    }

    fun removeFragment() {
        mFragmentList.removeLast()
        notifyItemChanged(mFragmentList.size)
    }
}