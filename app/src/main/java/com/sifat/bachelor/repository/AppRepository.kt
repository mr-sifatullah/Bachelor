package com.sifat.bachelor.repository

import com.sifat.bachelor.api.ApiInterfaceANA
import com.sifat.bachelor.api.ApiInterfacePush
import com.sifat.bachelor.api.model.NotificationData


class AppRepository(
    private val apiInterfaceANA: ApiInterfaceANA,
    private val apiInterfacePush: ApiInterfacePush
) {
    suspend fun userLogin(key: String) = apiInterfaceANA.userLogin(key)

    suspend fun getUserNotice(key: String) = apiInterfaceANA.getUserNotice(key)

    suspend fun getUserHomeRentInfo(key: String) = apiInterfaceANA.getUserHomeRentInfo(key)

    suspend fun getUserMealInfo(key: String) = apiInterfaceANA.getUserMealInfo(key)

    suspend fun sentPush(message: NotificationData) = apiInterfacePush.sendPush(message)

}