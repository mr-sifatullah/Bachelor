package com.sifat.bachelor.repository

import com.sifat.bachelor.api.ApiInterfaceANA


class AppRepository(
    private val apiInterfaceANA: ApiInterfaceANA
) {
    suspend fun userLogin(key: String) = apiInterfaceANA.userLogin(key)

    suspend fun getUserNotice(key: String) = apiInterfaceANA.getUserNotice(key)

    suspend fun getUserHomeRentInfo(key: String) = apiInterfaceANA.getUserHomeRentInfo(key)

    suspend fun getUserMealInfo(key: String) = apiInterfaceANA.getUserMealInfo(key)

}