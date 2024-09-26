package com.sifat.bachelor.home_rent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.core.UserData.ParsedSetData
import com.sifat.bachelor.DigitConverter
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentHomeRentBinding
import com.sifat.bachelor.home.HomeViewModel
import com.sifat.bachelor.meal.MealFragment
import com.sifat.bachelor.meal.MealRentAdapter
import org.koin.android.ext.android.inject
import timber.log.Timber

class HomeRentFragment : Fragment() {
    private var binding: FragmentHomeRentBinding? = null

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
        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.getUserHomeRentInfo().observe(viewLifecycleOwner, Observer { lists->
            parseRentData(lists)
        })
    }

    private fun initClickLister(){

    }




    private fun parseRentData(values: List<List<String>>){

        val rentList = mutableListOf<RentData>()

        for (i in 1 until values.size) {
            val row = values[i]
            rentList.add(
                RentData(
                    name = row.getOrNull(0) ?: "",
                    mobile = row.getOrNull(1) ?: "", // Mobile number is in the second column
                    rent = row.getOrNull(2) ?: "",
                    electricity = row.getOrNull(3),
                    water = row.getOrNull(4),
                    mama = row.getOrNull(5),
                    serviceCharge = row.getOrNull(6),
                    internet = row.getOrNull(7),
                    gas = row.getOrNull(8),
                    khala = row.getOrNull(9),
                    meal = row.getOrNull(10),
                    extra = row.getOrNull(11),
                    others = row.getOrNull(12),
                    total = row.getOrNull(13),
                    paid = row.getOrNull(14),
                    back = row.getOrNull(15),
                    garbage = row.getOrNull(16),
                    month = row.getOrNull(17)
                )
            )
        }
       val myData = rentList.find { it.mobile == SessionManager.userId }
        binding?.totalAmount?.text = DigitConverter.toBanglaDigit(myData?.total)
        binding?.month?.text = "(${myData?.month})"
        binding?.rent?.text = DigitConverter.toBanglaDigit(myData?.rent)
        binding?.water?.text = DigitConverter.toBanglaDigit(myData?.water)
        binding?.gas?.text = DigitConverter.toBanglaDigit(myData?.gas)
        binding?.electricity?.text = DigitConverter.toBanglaDigit(myData?.electricity)
        binding?.internet?.text = DigitConverter.toBanglaDigit(myData?.internet)
        binding?.mama?.text = DigitConverter.toBanglaDigit(myData?.mama)
        binding?.khala?.text = DigitConverter.toBanglaDigit(myData?.khala)
        binding?.extra?.text = DigitConverter.toBanglaDigit(myData?.extra)
        binding?.serviceCharge?.text = DigitConverter.toBanglaDigit(myData?.serviceCharge)
        binding?.meal?.text = DigitConverter.toBanglaDigit(myData?.meal)
        binding?.others?.text = DigitConverter.toBanglaDigit(myData?.others)
        binding?.garbege?.text = DigitConverter.toBanglaDigit(myData?.garbage)

        binding?.progressBar?.visibility = View.GONE
        binding?.detailsLayout?.visibility = View.VISIBLE
        binding?.takaIconText?.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

data class RentData(
    val name: String,
    val mobile: String,
    val rent: String,
    val electricity: String?,
    val water: String?,
    val mama: String?,
    val serviceCharge: String?,
    val internet: String?,
    val gas: String?,
    val khala: String?,
    val meal: String?,
    val extra: String?,
    val others: String?,
    val total: String?,
    val paid: String?,
    val back: String?,
    val garbage: String?,
    val month: String?
)
