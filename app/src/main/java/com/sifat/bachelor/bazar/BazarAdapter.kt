package com.sifat.bachelor.bazar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sifat.bachelor.databinding.ItemViewBazarListBinding
import com.sifat.bachelor.databinding.ItemViewHomeRentBinding
import com.sifat.bachelor.databinding.ItemViewMealBinding

class BazarAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<BazarRecord> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewBazarListBinding = ItemViewBazarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {

            val model = dataList[position]
            val binding = holder.binding

            binding.bazarAmount.text = "${model.bazarAmount}"
            binding.date.text = model.date
            binding.bazarDescription.text = model.description

        }
    }

    internal inner class ViewModel(val binding: ItemViewBazarListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }
    }

    fun initLoad(list: List<BazarRecord>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}