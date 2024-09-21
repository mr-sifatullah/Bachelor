package com.sifat.bachelor.meal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sifat.bachelor.databinding.ItemViewHomeRentBinding
import com.sifat.bachelor.databinding.ItemViewMealBinding

class MealRentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<Meal> = mutableListOf()
    var onItemClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewMealBinding = ItemViewMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {

            val model = dataList[position]
            val binding = holder.binding

            binding.title.text = model.date
            binding.amount.text = if (model.off) {"off"} else {"Lunch: ${model.lunch} Dinner: ${model.dinner}"}

        }
    }

    internal inner class ViewModel(val binding: ItemViewMealBinding) : RecyclerView.ViewHolder(binding.root) {
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