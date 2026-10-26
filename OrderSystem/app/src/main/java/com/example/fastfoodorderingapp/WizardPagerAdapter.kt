package com.example.fastfoodorderingapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WizardPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainMealFragment()
            1 -> SideDishesFragment()
            2 -> DrinkFragment()
            3 -> ConfirmFragment()
            else -> MainMealFragment()
        }
    }
}
