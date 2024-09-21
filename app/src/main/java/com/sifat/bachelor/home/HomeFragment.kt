package com.sifat.bachelor.home

import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        fetchTotalMealsForCurrentAndPreviousDays(1)
        //calculateMealRate()
        //fetchUserBazarCost()
        //fetchUserHomeRentCost()
        //fetchUserMealInfo()

    }

    private fun fetchTotalMealsForCurrentAndPreviousDays(daysBack: Int) {
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = getCurrentMonth() // Function to get the current month dynamically

        val monthPath = "${currentYear}-$currentMonth" // Format as "2024-09"

        val totalMealsMap = mutableMapOf<String, Int>()

        for (i in 0..daysBack) {
            calendar.add(Calendar.DAY_OF_MONTH, -i) // Go back i days
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val mealsRef = firestore.collection("meals")
                .document(monthPath)
                .collection("totals")
                .document(date)

            mealsRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val totalCount = document.getLong("totalMeal")?.toInt() ?: 0
                    totalMealsMap[date] = totalCount
                    Log.d("TotalMeals", "Total meals for $date: $totalCount")
                } else {
                    Log.d("TotalMeals", "No data found for $date.")
                    totalMealsMap[date] = 0 // Store 0 if no data is found
                }

                // Check if all requests are completed
                if (totalMealsMap.size == daysBack + 1) {
                    // You can now use totalMealsMap for further processing
                    Log.d("TotalMeals", "Fetched total meals: $totalMealsMap")
                }
            }.addOnFailureListener { e ->
                Log.e("TotalMeals", "Error fetching meal data: ${e.message}")
            }

            calendar.add(Calendar.DAY_OF_MONTH, i) // Reset calendar to the original date
        }
    }

    fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())  // Formats the month name (e.g., "September")
        return dateFormat.format(Date())  // Returns the current month as a string
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

        binding?.adminLayout?.setOnClickListener {
            findNavController().navigate(R.id.nav_admin)
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
