package id.fishku.consumer.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabOrderAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = OrderFragment()
        fragment.arguments = Bundle().apply {
            putInt(OrderFragment.SECTION_NUMBER, position + 1)
        }
        return fragment
    }
}