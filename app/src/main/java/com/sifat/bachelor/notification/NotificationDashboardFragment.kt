package com.sifat.bachelor.notification

import android.view.LayoutInflater
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.sifat.bachelor.databinding.FragmentNotificationDashboardBinding

class NotificationDashboardFragment : Fragment() {
    private var binding: FragmentNotificationDashboardBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentNotificationDashboardBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun fetchMealCounts() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
