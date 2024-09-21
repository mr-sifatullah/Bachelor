package com.sifat.bachelor.home_rent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sifat.bachelor.databinding.FragmentHomeRentBinding
import com.sifat.bachelor.meal.MealFragment
import com.sifat.bachelor.meal.MealRentAdapter

class HomeRentFragment : Fragment() {
    private var binding: FragmentHomeRentBinding? = null
    private  var dataAdapter: MealRentAdapter = MealRentAdapter()

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

        val bundle: Bundle? = arguments
        bundle?.let {
            titleList = bundle?.getStringArrayList("title") as List<String>
            dataList = bundle?.getStringArrayList("model") as List<String>
        }

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
        //dataAdapter.initLoad(titleList, dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}