package com.sifat.bachelor.home_rent

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sifat.bachelor.databinding.ItemViewBazarCostsBinding
import com.sifat.bachelor.databinding.ItemViewHomeRentBinding

class HomeRentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val titleList: MutableList<String> = mutableListOf()
    private val dataList: MutableList<String> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewHomeRentBinding = ItemViewHomeRentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val title = titleList[position]
            val model = dataList[position]
            val binding = holder.binding


            binding.title.text = title
            binding.amount.text = model

        }
    }

    internal inner class ViewModel(val binding: ItemViewHomeRentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }
    }

    fun initLoad(titleLists: List<String>, list: List<String>) {
        dataList.clear()
        titleList.clear()
        dataList.addAll(list)
        titleList.addAll(titleLists)
        notifyDataSetChanged()
    }
}