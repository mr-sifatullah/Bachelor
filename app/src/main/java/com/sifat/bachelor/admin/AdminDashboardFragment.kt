package com.sifat.bachelor.admin

import android.view.LayoutInflater
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.sifat.bachelor.databinding.FragmentAdminDashboardBinding
import com.sifat.bachelor.home.BazarRecord
import com.sifat.bachelor.home.MealRecord

class AdminDashboardFragment : Fragment() {
    private var binding: FragmentAdminDashboardBinding? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAdminDashboardBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //fetchMealCounts()
        //fetchBazarRecords()
        //fetchExtraBazarRecords()
        //calculateMealRate()
    }

    private fun fetchMealCounts() {
        firestore.collectionGroup("meals")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val mealCounts = StringBuilder()
                for (document in querySnapshot.documents) {
                    val userId = document.reference.parent.parent?.id // Get user ID
                    val mealRecord = document.toObject(MealRecord::class.java)
                    if (mealRecord != null) {
                        mealCounts.append("$userId: Lunch ${mealRecord.lunch}, Dinner ${mealRecord.dinner}\n")
                    }
                }
                binding?.tvMealCounts?.text = mealCounts.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch meal counts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBazarRecords() {
        firestore.collection("bazarRecords")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val bazarList = StringBuilder()
                for (document in querySnapshot.documents) {
                    val bazarRecord = document.toObject(BazarRecord::class.java)
                    if (bazarRecord != null) {
                        bazarList.append("Date: ${bazarRecord.date}, Amount: ${bazarRecord.bazarAmount}\n")
                    }
                }
                binding?.tvBazarList?.text = bazarList.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch bazar records: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchExtraBazarRecords() {
        // Assuming you have a separate collection for extra bazar
        firestore.collection("extraBazarRecords")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val extraBazarList = StringBuilder()
                for (document in querySnapshot.documents) {
                    val extraRecord = document.toObject(BazarRecord::class.java) // Change to appropriate data class if needed
                    if (extraRecord != null) {
                        extraBazarList.append("Date: ${extraRecord.date}, Extra Amount: ${extraRecord.extraAmount}\n")
                    }
                }
                binding?.tvExtraBazarList?.text = extraBazarList.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to fetch extra bazar records: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateMealRate() {
        var totalBazar = 0.0
        var totalMeals = 0

        firestore.collection("bazarRecords")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val bazarRecord = document.toObject(BazarRecord::class.java)
                    if (bazarRecord != null) {
                        totalBazar += bazarRecord.bazarAmount
                    }
                }
                firestore.collectionGroup("meals")
                    .get()
                    .addOnSuccessListener { mealSnapshot ->
                        for (document in mealSnapshot.documents) {
                            val mealRecord = document.toObject(MealRecord::class.java)
                            if (mealRecord != null) {
                                totalMeals += (mealRecord.lunch + mealRecord.dinner)
                            }
                        }
                        if (totalMeals > 0) {
                            val mealRate = totalBazar / totalMeals
                            binding?.tvMealRate?.text = String.format("Meal Rate: %.2f", mealRate)
                        } else {
                            binding?.tvMealRate?.text = "Meal Rate: N/A (No meals recorded)"
                        }
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to calculate meal rate: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
