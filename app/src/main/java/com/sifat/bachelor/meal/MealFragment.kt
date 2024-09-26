package com.sifat.bachelor.meal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sifat.bachelor.CustomSpinnerAdapter
import com.sifat.bachelor.R
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentMealBinding
import com.sifat.bachelor.getCurrentDate
import com.sifat.bachelor.getCurrentMonth
import com.sifat.bachelor.toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MealFragment : Fragment() {

    private var binding: FragmentMealBinding? = null
    private lateinit var firestore: FirebaseFirestore

    var selectedLunchIndex = -1
    var selectedLunchCount = ""
    var selectedDinnerIndex = -1
    var selectedDinnerCount = ""

    private  var dataAdapter: MealRentAdapter = MealRentAdapter()
    private  var countDataAdapter: MealCountAdapter = MealCountAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentMealBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        firestore = FirebaseFirestore.getInstance()

        setUpeLunchSpinner()
        setUpeDinnerSpinner()

        binding?.datePicker?.setOnClickListener {
            showDatePicker()
        }
        initView()
        binding?.btnRecordMeal?.setOnClickListener {
            onRecordMealButtonClick()
        }

        binding?.btnShowMeals?.setOnClickListener {
            binding?.countLayout?.visibility = View.GONE
            onShowMealsButtonClick()
        }

        binding?.btnMealCount?.setOnClickListener {
            onShowMealsCountButtonClick()
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
        binding?.recycleViewMealCount?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = countDataAdapter
            }
        }
    }

    private fun setUpeLunchSpinner() {

        val pickupLunchList: MutableList<String> = mutableListOf()
        pickupLunchList.add("Lunch")
        pickupLunchList.add("0")
        pickupLunchList.add("1")
        pickupLunchList.add("2")
        pickupLunchList.add("3")
        pickupLunchList.add("4")

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupLunchList)
        binding?.spinnerLunch?.adapter = spinnerAdapter
        binding?.spinnerLunch?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLunchIndex = position
                selectedLunchCount = pickupLunchList[position]
            }
        }
    }

    private fun setUpeDinnerSpinner() {

        val pickupDinnerList: MutableList<String> = mutableListOf()
        pickupDinnerList.add("Dinner")
        pickupDinnerList.add("0")
        pickupDinnerList.add("1")
        pickupDinnerList.add("2")
        pickupDinnerList.add("3")
        pickupDinnerList.add("4")

        val spinnerAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, pickupDinnerList)
        binding?.spinnerDinner?.adapter = spinnerAdapter
        binding?.spinnerDinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDinnerIndex = position
                selectedDinnerCount = pickupDinnerList[position]
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
        currentMonthEnd.set(Calendar.DAY_OF_MONTH, currentMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            binding?.datePicker?.setText(formattedDate)
        }, year, month, day)


        // Set the date picker limits
        datePickerDialog.datePicker.minDate = currentMonthStart.timeInMillis
        datePickerDialog.datePicker.maxDate = currentMonthEnd.timeInMillis

        datePickerDialog.show()
    }

    private fun onRecordMealButtonClick() {
        val userName = SessionManager.userName
        val mobileNumber = SessionManager.userId
        val date = binding?.datePicker?.text.toString().trim()


        if (date.isEmpty()) {
            context?.toast("Please select a date.")
            return
        }

        if (selectedLunchIndex < 1) {
            context?.toast("Please select Lunch.")
            return
        }

        if (selectedDinnerIndex < 1) {
            context?.toast("Please select Dinner.")
            return
        }

        recordMeal(userName, mobileNumber, date, selectedLunchCount.toInt(), selectedDinnerCount.toInt())
    }

    private fun recordMeal(userName: String, mobileNumber: String, date: String, lunchCount: Int, dinnerCount: Int) {
        if (lunchCount !in 0..4 || dinnerCount !in 0..4) {
            Toast.makeText(context, "Meal count should be between 0 and 4", Toast.LENGTH_SHORT).show()
            return
        }

        val userMealRef = FirebaseFirestore.getInstance()
            .collection("mealsRecords")
            .document(getCurrentMonth()) // Current month (e.g., "September")

        // Create a unique document ID using mobile number and date
        val documentId = "${mobileNumber}_$date"

        // Check if the meal record for the user on that date already exists
        userMealRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val existingMeals = documentSnapshot.data?.keys?.toList() ?: emptyList()
                if (existingMeals.contains(documentId)) {
                    // Record already exists for this user on this date
                    Toast.makeText(context, "You have already recorded your meal for this date.", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed to add the record as no record exists for this date
                    val mealData = hashMapOf(
                        "date" to date,
                        "userName" to userName,
                        "lunch" to lunchCount,
                        "dinner" to dinnerCount,
                        "mobileNumber" to mobileNumber
                    )

                    // Add the meal data to Firestore under the current month document
                    userMealRef.set(hashMapOf(documentId to mealData), SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Meal recorded successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to record meal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, "No data found for the current month.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to check record: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onShowMealsButtonClick() {
        val mobileNumber = SessionManager.userId
        val currentMonth = getCurrentMonth()

        if (mobileNumber.isNotEmpty()) {
            fetchMealData(currentMonth, mobileNumber) { meals ->
                if (meals != null && meals.isNotEmpty()) {
                    binding?.recycleView?.visibility = View.VISIBLE
                    binding?.titleLayout?.visibility = View.VISIBLE
                    binding?.totalMeal?.text =  "Total Meal : ${meals.sumOf { it.lunch + it.dinner }}"
                    dataAdapter.initLoad(meals)
                } else {
                    binding?.titleLayout?.visibility = View.GONE
                    Toast.makeText(context, "No meal data found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun onShowMealsCountButtonClick() {
        val currentMonth = getCurrentMonth()

        fetchMealCountData(currentMonth) { meal ->
                if (!meal.isNullOrEmpty()) {
                    binding?.recycleView?.visibility = View.GONE
                    binding?.titleLayout?.visibility = View.GONE
                    binding?.recycleViewMealCount?.visibility = View.VISIBLE
                    binding?.countLayout?.visibility = View.VISIBLE
                    binding?.totalMealCount?.text =  "${meal.sumOf { it.lunch + it.dinner }}"
                    countDataAdapter.initLoad(meal)

                }else{
                    Toast.makeText(context, "No meal data found", Toast.LENGTH_SHORT).show()
                }
        }


    }

    private fun fetchMealData(currentMonth: String, mobileNumber: String, callback: (List<Meal>?) -> Unit) {
        val mealsRef = FirebaseFirestore.getInstance()
            .collection("mealsRecords")
            .document(currentMonth)

        mealsRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val meals = mutableListOf<Meal>()
                for (key in documentSnapshot.data?.keys ?: emptyList()) {
                    val mealData = documentSnapshot.get(key) as? Map<String, Any>
                    if (mealData != null && mealData["mobileNumber"] == mobileNumber) {
                        meals.add(
                            Meal(
                                date = mealData["date"] as? String ?: "",
                                lunch = (mealData["lunch"] as? Long)?.toInt() ?: 0,
                                dinner = (mealData["dinner"] as? Long)?.toInt() ?: 0,
                                userName = mealData["userName"] as? String ?: "",
                                mobileNumber = mealData["mobileNumber"] as? String ?: ""
                            )
                        )
                    }
                }
                meals.sortBy { it.date }
                callback(if (meals.isNotEmpty()) meals else null)
            } else {
                callback(null)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to retrieve meal data: ${e.message}", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }

    private fun fetchMealCountData(currentMonth: String, callback: (List<Meal>?) -> Unit) {
        val currentDate = getCurrentDate()

        val mealsRef = FirebaseFirestore.getInstance()
            .collection("mealsRecords")
            .document(currentMonth)

        mealsRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val meals = mutableListOf<Meal>()
                for (key in documentSnapshot.data?.keys ?: emptyList()) {
                    val mealData = documentSnapshot.get(key) as? Map<String, Any>
                    if (mealData != null && mealData["date"] == currentDate) {
                        meals.add(
                            Meal(
                                date = mealData["date"] as? String ?: "",
                                lunch = (mealData["lunch"] as? Long)?.toInt() ?: 0,
                                dinner = (mealData["dinner"] as? Long)?.toInt() ?: 0,
                                userName = mealData["userName"] as? String ?: "",
                                mobileNumber = mealData["mobileNumber"] as? String ?: ""
                            )
                        )
                    }
                }
                meals.sortBy { it.date }
                callback(if (meals.isNotEmpty()) meals else null)
            } else {
                callback(null)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to retrieve meal data: ${e.message}", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

data class Meal(
    val date: String,
    val lunch: Int,
    val dinner: Int,
    val userName: String,
    val mobileNumber: String
)


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
