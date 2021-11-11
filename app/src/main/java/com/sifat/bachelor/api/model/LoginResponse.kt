package com.sifat.bachelor.api.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("majorDimension")
    val majorDimension: String = "",
    @SerializedName("range")
    val range: String = "",
    @SerializedName("values")
    val values: List<List<String>> = listOf()
)