package com.sifat.bachelor.home_rent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentHomeRentBinding
import com.sifat.bachelor.home.HomeViewModel
import com.sifat.bachelor.meal.MealFragment
import com.sifat.bachelor.meal.MealRentAdapter
import org.koin.android.ext.android.inject

class HomeRentFragment : Fragment() {
    private var binding: FragmentHomeRentBinding? = null
    private  var dataAdapter: HomeRentAdapter = HomeRentAdapter()

    private val viewModel: HomeViewModel by inject()

    private var titleList: List<String> = listOf()
    private var dataList: List<String> = listOf()

    companion object {
        fun newInstance(): MealFragment = MealFragment().apply {}
        val tag: String = MealFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeRentBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initView()
        initClickLister()
    }

    private fun initView() {
        binding?.recycleView?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }
    }

    private fun initClickLister(){
        viewModel.getUserHomeRentInfo().observe(viewLifecycleOwner, Observer { lists->
            lists.forEach { list->
                if (list.contains(SessionManager.userName)){
                    dataList = list
                }
                if (list.contains("Name")){
                    titleList = list
                }
            }
        })
        dataAdapter.initLoad(titleList, dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}