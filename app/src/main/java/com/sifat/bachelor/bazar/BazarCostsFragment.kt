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
import com.sifat.bachelor.databinding.FragmentBazarCostsBinding
import java.util.Calendar
class BazarCostsFragment : Fragment() {
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

        // Save bazar data in Firestore
        val bazarData = hashMapOf(
            "date" to date,
            "bazarAmount" to bazarAmount,
            "extraAmount" to extraAmount,
            "description" to description
        )

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Clear binding when view is destroyed
    }
}

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
