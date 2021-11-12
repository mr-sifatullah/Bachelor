package com.sifat.bachelor.bazar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sifat.bachelor.databinding.FragmentBazarCostsBinding

class BazarCostsFragment : Fragment() {
    private var binding: FragmentBazarCostsBinding? = null
    private  var dataAdapter: BazarCostsAdapter = BazarCostsAdapter()

    private var dataList: List<String> = listOf()

    companion object {
        fun newInstance(): BazarCostsFragment = BazarCostsFragment().apply {}
        val tag: String = BazarCostsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBazarCostsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle: Bundle? = arguments
        bundle?.let {
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
        dataAdapter.initLoad(dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}