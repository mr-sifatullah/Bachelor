package com.sifat.bachelor.bazar

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentBazarCostsBinding
import com.sifat.bachelor.getCurrentMonth
import com.sifat.bachelor.hideKeyboard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BazarCostsFragment : Fragment() {

    private var binding: FragmentBazarCostsBinding? = null
    private var totalBazar: Double = 0.0
    private  var dataAdapter: BazarAdapter = BazarAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBazarCostsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding?.recycleView?.let { view ->
            with(view) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
            }
        }

        binding?.datePicker?.setOnClickListener {
            showDatePicker()
        }

        binding?.btnRecordBazar?.setOnClickListener {
            hideKeyboard()
            recordBazar()
        }
        binding?.btnShowBazar?.setOnClickListener {
            hideKeyboard()
            loadBazarRecords()
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

    private fun recordBazar() {
        val date = binding?.datePicker?.text.toString().trim()
        val bazarAmount = binding?.etBazarAmount?.text.toString().trim().toDoubleOrNull() ?: 0.0
        val extraAmount = binding?.etExtraAmount?.text.toString().trim().toDoubleOrNull() ?: 0.0
        val description = binding?.bazarDescriptionTV?.text.toString().trim()

        // Validate inputs
        if (date.isEmpty()) {
            Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }

        if (bazarAmount <= 0) {
            Toast.makeText(context, "Bazar amount must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (extraAmount < 0) {
            Toast.makeText(context, "Extra amount cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(context, "Description cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val mobileNumber = SessionManager.userId
        val userName = SessionManager.userName

        val userBazarRef = FirebaseFirestore.getInstance()
            .collection("bazarRecords")
            .document(getCurrentMonth()) // Current month (e.g., "September")

        // Create a unique document ID using mobile number and date
        val documentId = "${mobileNumber}_$date"

        // Check if the bazar record for the user on that date already exists
        userBazarRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val existingRecords = documentSnapshot.data?.keys?.toList() ?: emptyList()
                if (existingRecords.contains(documentId)) {
                    // Record already exists for this user on this date
                    Toast.makeText(context, "You have already recorded your bazar for this date.", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed to add the record as no record exists for this date
                    val bazarData = hashMapOf(
                        "date" to date,
                        "userName" to userName,
                        "bazarAmount" to bazarAmount,
                        "extraAmount" to extraAmount,
                        "description" to description,
                        "mobileNumber" to mobileNumber
                    )

                    // Add the bazar data to Firestore under the current month document
                    userBazarRef.set(hashMapOf(documentId to bazarData), SetOptions.merge())
                        .addOnSuccessListener {
                            clearInputs()
                            Toast.makeText(context, "Bazar recorded successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to record bazar: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // If the document does not exist, create it
                val bazarData = hashMapOf(
                    documentId to hashMapOf(
                        "date" to date,
                        "userName" to userName,
                        "bazarAmount" to bazarAmount,
                        "extraAmount" to extraAmount,
                        "description" to description,
                        "mobileNumber" to mobileNumber
                    )
                )

                userBazarRef.set(bazarData, SetOptions.merge())
                    .addOnSuccessListener {
                        clearInputs()
                        Toast.makeText(context, "Bazar recorded successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to record bazar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to check record: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputs() {
        binding?.datePicker?.setText("")
        binding?.etBazarAmount?.setText("")
        binding?.etExtraAmount?.setText("")
        binding?.bazarDescriptionTV?.setText("")
    }

    private fun loadBazarRecords() {
        // Reference to the bazarRecords document for the current month
        val bazarRef = FirebaseFirestore.getInstance()
            .collection("bazarRecords")
            .document(getCurrentMonth()) // Current month (e.g., "September")

        // Fetching the document for the current month
        bazarRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val bazarList = mutableListOf<BazarRecord>()
                // Loop through the keys (which are the document IDs)
                for (key in documentSnapshot.data?.keys ?: emptyList()) {
                    val bazarData = documentSnapshot.get(key) as? Map<String, Any>
                    if (bazarData != null && bazarData["mobileNumber"] == SessionManager.userId) {
                        bazarList.add(
                            BazarRecord(
                                date = bazarData["date"] as? String ?: "",
                                bazarAmount = (bazarData["bazarAmount"] as? Double) ?: 0.0,
                                extraAmount = (bazarData["extraAmount"] as? Double) ?: 0.0,
                                description = bazarData["description"] as? String ?: "",
                                userName = bazarData["userName"] as? String ?: "",
                                mobileNumber = bazarData["mobileNumber"] as? String ?: ""
                            )
                        )
                    }
                }
                bazarList.sortBy { it.date }
                // Load the data into the adapter
                dataAdapter.initLoad(bazarList)
            } else {
                Toast.makeText(context, "No data found for the current month.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to retrieve bazar records: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Clear binding when view is destroyed
    }
}

data class BazarRecord(
    val date: String = "",
    val userName: String = "",
    val bazarAmount: Double = 0.0,
    val extraAmount: Double = 0.0,
    val description: String = "",
    val mobileNumber: String = ""
)



/*class BazarManagementFragment : Fragment() {
    private var binding: FragmentBazarCostsBinding? = null
    private var totalBazar: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBazarCostsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding?.btnSelectBazarDate?.setOnClickListener {
            showDatePicker()
        }

        binding?.btnRecordBazar?.setOnClickListener {
            recordBazar()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding?.etBazarDate?.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun recordBazar() {
        val date = binding?.etBazarDate?.text.toString().trim()
        val bazarAmount = binding?.etBazarAmount?.text.toString().trim().toDoubleOrNull() ?: 0.0
        val extraAmount = binding?.etExtraAmount?.text.toString().trim().toDoubleOrNull() ?: 0.0
        val description = binding?.etDescription?.text.toString().trim()

        if (date.isEmpty()) {
            Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
            return
        }

        if (bazarAmount < 0 || extraAmount < 0) {
            Toast.makeText(context, "Amounts cannot be negative.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate total bazar
        totalBazar += bazarAmount + extraAmount
        binding?.tvTotalBazar?.text = "Total Bazar Amount: $totalBazar"

        // Save bazar data in Firestore (if needed)
        val bazarData = hashMapOf(
            "date" to date,
            "bazarAmount" to bazarAmount,
            "extraAmount" to extraAmount,
            "description" to description
        )

        // Replace "your_collection_name" with the actual collection name
        FirebaseFirestore.getInstance().collection("bazarRecords").add(bazarData)
            .addOnSuccessListener {
                Toast.makeText(context, "Bazar recorded successfully!", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to record bazar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputs() {
        binding?.etBazarDate?.setText("")
        binding?.etBazarAmount?.setText("")
        binding?.etExtraAmount?.setText("")
        binding?.etDescription?.setText("")
    }
}*/


/*
class BazarCostsFragment : Fragment() {
    private var binding: FragmentBazarCostsBinding? = null
    private  var dataAdapter: BazarCostsAdapter = BazarCostsAdapter()

    private var dataList: List<String> = listOf()

    companion object {
        fun newInstance(): BazarCostsFragment = BazarCostsFragment().apply {}
        val tag: String = BazarCostsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentBazarCostsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle: Bundle? = arguments
        bundle?.let {
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
        dataAdapter.initLoad(dataList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}*/
