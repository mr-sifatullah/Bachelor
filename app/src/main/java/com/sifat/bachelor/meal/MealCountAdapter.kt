package com.sifat.bachelor.meal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sifat.bachelor.databinding.ItemViewMealCountBinding

class MealCountAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<Meal> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewMealCountBinding = ItemViewMealCountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {

            val model = dataList[position]
            val binding = holder.binding

            binding.count.text = "${model.lunch}"
            binding.dinnerCount.text = "${model.dinner}"
            binding.name.text = model.userName

        }
    }

    internal inner class ViewModel(val binding: ItemViewMealCountBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {

            }
        }
    }

    fun initLoad(list: List<Meal>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}