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

        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        setupMealTypeSelection()

        // Set up spinners for lunch and dinner counts
        val lunchCounts = (0..4).toList().map { it.toString() }
        val dinnerCounts = (0..4).toList().map { it.toString() }

        binding?.spinnerLunchCount?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lunchCounts)
        binding?.spinnerDinnerCount?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dinnerCounts)

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

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding?.etDate?.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupMealTypeSelection() {
        binding?.radioGroupMealType?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioLunch, R.id.radioDinner -> {
                    // Show dropdowns for lunch and dinner counts
                    binding?.mealCountLayout?.visibility = View.VISIBLE
                }
                R.id.radioOff -> {
                    // Hide dropdowns for meal counts
                    binding?.mealCountLayout?.visibility = View.GONE
                }
            }
        }
    }



    private fun onRecordMealButtonClick() {
        val userName = SessionManager.userName
        val mobileNumber = SessionManager.userId
        val date = binding?.etDate?.text.toString().trim()
        val lunchCount = if (binding?.radioLunch?.isChecked == true) {
            binding?.spinnerLunchCount?.selectedItem.toString().toInt()
        } else 0
        val dinnerCount = if (binding?.radioDinner?.isChecked == true) {
            binding?.spinnerDinnerCount?.selectedItem.toString().toInt()
        } else 0
        val isOff = binding?.radioOff?.isChecked == true

        // Validation
        if (date.isEmpty()) {
            Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding?.radioLunch?.isChecked!! && !binding?.radioDinner?.isChecked!! && !isOff) {
            Toast.makeText(context, "Please select at least one meal type.", Toast.LENGTH_SHORT).show()
            return
        }

        // Call the recordMeal function with the collected data
        recordMeal(userName, mobileNumber, date, lunchCount, dinnerCount, isOff)
    }


    private fun recordMeal(
        userName: String,
        mobileNumber: String,
        date: String,
        lunchCount: Int,
        dinnerCount: Int,
        isOff: Boolean
    ) {
        // Validate meal limits
        if (lunchCount !in 0..4 || dinnerCount !in 0..4) {
            Toast.makeText(context, "Meal count should be between 0 and 4", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the meal document for the specific date
        val mealDocRef = firestore.collection("users").document(mobileNumber)
            .collection("meals").document(date)

        // Check if meal data for the given date already exists
        mealDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                Toast.makeText(context, "You have already submitted data for this date.", Toast.LENGTH_SHORT).show()
            } else {
                // Create meal data with the given date
                val mealData = hashMapOf(
                    "name" to userName,
                    "lunch" to lunchCount,
                    "dinner" to dinnerCount,
                    "off" to isOff,
                    "date" to date
                )

                // Store meal data in 'meals' collection under the user document for the given date
                mealDocRef.set(mealData)
                    .addOnSuccessListener {
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

    private fun onShowMealsButtonClick() {
        val mobileNumber = SessionManager.userId

        if (mobileNumber.isNotEmpty()) {
            getMealData(mobileNumber) { mealList ->
                dataAdapter.initLoad(mealList)
            }
        } else {
            Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMealData(mobileNumber: String, callback: (List<Meal>) -> Unit) {
        firestore.collection("users").document(mobileNumber)
            .collection("meals")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val mealList = mutableListOf<Meal>()
                for (document in querySnapshot.documents) {
                    val lunch = document.getLong("lunch") ?: 0
                    val dinner = document.getLong("dinner") ?: 0
                    val off = document.getBoolean("off") ?: false
                    val date = document.getString("date") ?: ""

                    // Add meal data to the list
                    mealList.add(Meal(date, lunch.toInt(), dinner.toInt(), off))
                }
                callback(mealList)  // Return the meal list via callback
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to retrieve meal data: ${e.message}", Toast.LENGTH_SHORT).show()
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
