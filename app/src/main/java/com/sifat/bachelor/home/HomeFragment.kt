package com.sifat.bachelor.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.sifat.bachelor.R
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentHomeBinding
import com.sifat.bachelor.toast
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by inject()

    private var bazarCosts: List<String> = listOf()
    private var homeRentTitleCosts: List<String> = listOf()
    private var homeRentCosts: List<String> = listOf()
    private var mealTitleCosts: List<String> = listOf()
    private var mealCosts: List<String> = listOf()
    var totalBazar = 0.0
    var totalMeals = 0
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        fun newInstance() : HomeFragment = HomeFragment().apply{}
        val tag: String = HomeFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentHomeBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickLister()
        calculateMealRate()
        //fetchUserBazarCost()
        //fetchUserHomeRentCost()
        //fetchUserMealInfo()

    }

    private fun initView(){
        binding?.userName?.text = if (SessionManager.userName.isNotEmpty()){SessionManager.userName}else{"User Name"}
        binding?.userMobile?.text = if (SessionManager.userId.isNotEmpty()){"0${SessionManager.userId}"}else{"01XXXXXXXXX"}
    }

    private fun initClickLister(){

        binding?.logoutLayout?.setOnClickListener {
            logout()
        }

        binding?.bazarCostLayout?.setOnClickListener {
            goToUserBazarCosts()
        }
        binding?.homeRentLayout?.setOnClickListener {
            goToHomeRentCosts()
        }

        binding?.mealLayout?.setOnClickListener {
            goToMealInfo()
        }

    }

    private fun fetchUserBazarCost(){
        viewModel.getUserBazarInfo().observe(viewLifecycleOwner, Observer { lists->
            lists.forEach { list->
                if (list.contains(SessionManager.userName)){
                    bazarCosts = list
                }
            }
        })
    }
    private fun fetchUserHomeRentCost(){
        viewModel.getUserHomeRentInfo().observe(viewLifecycleOwner, Observer { lists->
            lists.forEach { list->
                if (list.contains(SessionManager.userName)){
                    homeRentCosts = list
                }
                if (list.contains("Name")){
                    homeRentTitleCosts = list
                }
            }
        })
    }

    private fun fetchUserMealInfo(){
        viewModel.getUserMealInfo().observe(viewLifecycleOwner, Observer { lists->
            lists.forEach { list->
                if (list.contains(SessionManager.userName)){
                    mealCosts = list
                }
                if (list.contains("Date")){
                    mealTitleCosts = list
                }
            }
        })
    }

    private fun goToUserBazarCosts(){
        val bundle = bundleOf(
            "model" to bazarCosts
        )
        findNavController().navigate(R.id.nav_bazar_costs, bundle)
    }

    private fun goToHomeRentCosts(){
        val bundle = bundleOf(
            "title" to homeRentTitleCosts,
            "model" to homeRentCosts
        )
        findNavController().navigate(R.id.nav_home_rent, bundle)
    }

    private fun goToMealInfo(){
        val bundle = bundleOf(
            "title" to mealTitleCosts,
            "model" to mealCosts
        )
        findNavController().navigate(R.id.nav_meal, bundle)
    }


    private fun logout(){
        SessionManager.clearSession()
        if (activity != null) {
            (activity as HomeActivity).goToLogin()
        }
    }

    private fun calculateMealRate() {


        // Fetch bazar records
        firestore.collection("bazarRecords")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val bazarRecord = document.toObject(BazarRecord::class.java)
                    if (bazarRecord != null) {
                        totalBazar += bazarRecord.bazarAmount
                    }
                }
                // After fetching bazar records, fetch meal records
                fetchMealRecords(totalBazar)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch bazar records: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMealRecords(totalBazar: Double) {
        firestore.collectionGroup("meals") // Using collectionGroup to fetch meals from all users
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val mealRecord = document.toObject(MealRecord::class.java)
                    if (mealRecord != null) {
                        totalMeals += (mealRecord.lunch + mealRecord.dinner)
                    }
                }
                // Calculate meal rate
                if (totalMeals > 0) {
                    val mealRate = totalBazar / totalMeals
                    binding?.meal?.text = String.format("Meal %.2f", mealRate)
                } else {
                    binding?.meal?.text = "Meal (No meals recorded)"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch meal records: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}


data class BazarRecord(
    val date: String = "",
    val bazarAmount: Double = 0.0,
    val extraAmount: Double = 0.0,
    val description: String = ""
)

data class MealRecord(
    val lunch: Int = 0,
    val dinner: Int = 0,
    val isOff: Boolean = false
)
