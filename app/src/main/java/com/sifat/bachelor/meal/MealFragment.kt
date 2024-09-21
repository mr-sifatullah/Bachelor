package com.sifat.bachelor.meal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sifat.bachelor.R
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentMealBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MealFragment : Fragment() {
    private var binding: FragmentMealBinding? = null
    private lateinit var firestore: FirebaseFirestore

    private  var dataAdapter: MealRentAdapter = MealRentAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentMealBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        firestore = FirebaseFirestore.getInstance()

        binding?.btnSelectDate?.setOnClickListener {
            showDatePicker()
        }
        initView()
        binding?.btnRecordMeal?.setOnClickListener {
            onRecordMealButtonClick()
        }

        binding?.btnShowMeals?.setOnClickListener {
            onShowMealsButtonClick()
        }
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val currentMonthStart = calendar.clone() as Calendar
        currentMonthStart.set(Calendar.DAY_OF_MONTH, 1)

        val currentMonthEnd = calendar.clone() as Calendar
        currentMonthEnd.set(Calendar.DAY_OF_MONTH, currentMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH)) // End of the month

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding?.dateTF?.setText(formattedDate)
        }, year, month, day)

        // Set the date picker limits
        datePickerDialog.datePicker.minDate = currentMonthStart.timeInMillis
        datePickerDialog.datePicker.maxDate = currentMonthEnd.timeInMillis

        datePickerDialog.show()
    }


    private fun onRecordMealButtonClick() {
        val userName = SessionManager.userName
        val mobileNumber = SessionManager.userId
        val date = binding?.dateTF?.text.toString().trim()
        val lunchCount = binding?.lunchET?.text.toString().trim().toInt()
        val dinnerCount = binding?.lunchET?.text.toString().trim().toInt()

        if (date.isEmpty()) {
            Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }

        recordMeal(userName, mobileNumber, date, lunchCount, dinnerCount)
    }


    private fun recordMeal(
        userName: String,
        mobileNumber: String,
        date: String,
        lunchCount: Int,
        dinnerCount: Int
    ) {
        // Validate meal limits
        if (lunchCount !in 0..4 || dinnerCount !in 0..4) {
            Toast.makeText(context, "Meal count should be between 0 and 4", Toast.LENGTH_SHORT).show()
            return
        }

        val currentMonth = getCurrentMonth()

        // Reference to the meal document for the specific user and date under the current month
        val mealDocRef = firestore.collection("meals")
            .document(currentMonth)
            .collection(date)
            .document(mobileNumber)

        mealDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                Toast.makeText(context, "You have already submitted data for this date.", Toast.LENGTH_SHORT).show()
            } else {
                val mealData = hashMapOf(
                    "userName" to userName,
                    "lunch" to lunchCount,
                    "dinner" to dinnerCount,
                    "date" to date
                )

                mealDocRef.set(mealData)
                    .addOnSuccessListener {
                        updateTotalMealsForDate(date) // Update total meals after recording
                        Toast.makeText(context, "Meal recorded successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to record meal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error checking existing meal data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())  // Formats the month name (e.g., "September")
        return dateFormat.format(Date())  // Returns the current month as a string
    }


    private fun updateTotalMealsForDate(date: String) {
        val currentMonth = getCurrentMonth()

        // Reference to the collection for the current month and specific date
        val dateMealsRef = firestore.collection("meals")
            .document(currentMonth)
            .collection(date)

        dateMealsRef.get().addOnSuccessListener { querySnapshot ->
            var totalMeals = 0

            // Sum all users' lunch and dinner counts for the date
            for (document in querySnapshot.documents) {
                val lunch = document.getLong("lunch") ?: 0
                val dinner = document.getLong("dinner") ?: 0
                totalMeals += lunch.toInt() + dinner.toInt()
            }

            // Now store the total meal count in the main meals collection
            val totalMealsDocRef = firestore.collection("meals")
                .document(currentMonth)
                .collection("totals")  // A separate collection to store total meals per date
                .document(date)

            // Create or update the document for this date
            val totalData = hashMapOf(
                "date" to date,
                "totalMeal" to totalMeals
            )

            totalMealsDocRef.set(totalData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Total meals updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update total meals: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Error retrieving meals for total calculation: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onShowMealsButtonClick() {
        val mobileNumber = SessionManager.userId

        if (mobileNumber.isNotEmpty()) {
            /*getMealData(mobileNumber) { mealList ->
                dataAdapter.initLoad(mealList)
            }*/
        } else {
            Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
///*class MealFragment : Fragment() {
//
//    private lateinit var binding: FragmentMealBinding
//    private lateinit var firestore: FirebaseFirestore
//
//    private lateinit var datePickerDialog: DatePickerDialog
//    private var calendar: Calendar? = null
//    private var date = ""
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentMealBinding.inflate(inflater, container, false)
//        firestore = FirebaseFirestore.getInstance()
//        return binding.root
//    }
//
//    // Function to record meals for a specific date
//    private fun recordMeal(
//        userName: String,
//        mobileNumber: String,
//        date: String,
//        lunchCount: Int,
//        dinnerCount: Int,
//        isOff: Boolean
//    ) {
//        // Validate meal limits
//        if (lunchCount !in 0..4 || dinnerCount !in 0..4) {
//            Toast.makeText(context, "Meal count should be between 0 and 4", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Create meal data with the given date
//        val mealData = hashMapOf(
//            "name" to userName,
//            "lunch" to lunchCount,
//            "dinner" to dinnerCount,
//            "off" to isOff,
//            "date" to date
//        )
//
//        // Store meal data in 'meals' collection under the user document for the given date
//        firestore.collection("users").document(mobileNumber)
//            .collection("meals").document(date)  // Date as document ID (e.g., YYYYMMDD)
//            .set(mealData)
//            .addOnSuccessListener {
//                Toast.makeText(context, "Meal recorded successfully!", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context, "Failed to record meal: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    // Function to retrieve meals for a user
//    private fun getMealData(mobileNumber: String, callback: (List<Meal>) -> Unit) {
//        firestore.collection("users").document(mobileNumber)
//            .collection("meals")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                val mealList = mutableListOf<Meal>()
//                for (document in querySnapshot.documents) {
//                    val lunch = document.getLong("lunch") ?: 0
//                    val dinner = document.getLong("dinner") ?: 0
//                    val off = document.getBoolean("off") ?: false
//                    val date = document.getString("date") ?: ""
//
//                    // Add meal data to the list
//                    mealList.add(Meal(date, lunch.toInt(), dinner.toInt(), off))
//                }
//                callback(mealList)  // Return the meal list via callback
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(context, "Failed to retrieve meal data: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//
//    // Example of showing meal data
//    private fun onShowMealsButtonClick() {
//        val mobileNumber = SessionManager.userId
//
//        if (mobileNumber.isNotEmpty()) {
//            getMealData(mobileNumber) { mealList ->
//                mealList.forEach {
//                    Log.d("Meal", "Date: ${it.date}, Lunch: ${it.lunch}, Dinner: ${it.dinner}, Off: ${it.off}")
//                }
//            }
//        } else {
//            Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        // Initialize spinners, buttons, and date picker
//        setupMealTypeSelection()
//
//        // Set up spinners for lunch and dinner counts
//        val lunchCounts = (0..4).toList().map { it.toString() }
//        val dinnerCounts = (0..4).toList().map { it.toString() }
//
//        binding.spinnerLunchCount.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lunchCounts)
//        binding.spinnerDinnerCount.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dinnerCounts)
//
//        binding.btnSelectDate.setOnClickListener {
//            showDatePicker()
//        }
//
//        binding.btnRecordMeal.setOnClickListener {
//            onRecordMealButtonClick()
//        }
//
//        binding.btnShowMeals.setOnClickListener {
//            onShowMealsButtonClick()
//        }
//    }
//
//    private fun showDatePicker() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
//            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
//            binding.etDate.setText(formattedDate)
//        }, year, month, day)
//
//        datePickerDialog.show()
//    }
//    private fun setupMealTypeSelection() {
//        binding.radioGroupMealType.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.radioLunch, R.id.radioDinner -> {
//                    // Show dropdowns for lunch and dinner counts
//                    binding.mealCountLayout.visibility = View.VISIBLE
//                }
//                R.id.radioOff -> {
//                    // Hide dropdowns for meal counts
//                    binding.mealCountLayout.visibility = View.GONE
//                }
//            }
//        }
//    }
//}*/

// Data class for representing a meal
data class Meal(val date: String, val lunch: Int, val dinner: Int, val off: Boolean)

/*
class MealFragment : Fragment() {
    private var binding: FragmentMealBinding? = null
    private  var dataAdapter: MealRentAdapter = MealRentAdapter()

    private var titleList: List<String> = listOf()
    private var dataList: List<String> = listOf()

    companion object {
        fun newInstance(): MealFragment = MealFragment().apply {}
        val tag: String = MealFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentMealBinding.inflate(inflater, container, false).also {
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
        dataAdapter.initLoad(titleList, dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}*/
