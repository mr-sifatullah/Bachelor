package com.sifat.bachelor.api


import com.haroldadmin.cnradapter.NetworkResponse
import com.sifat.bachelor.api.model.Notification
import com.sifat.bachelor.api.model.NotificationData
import com.sifat.bachelor.fcm.AccessToken
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterfacePush {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfacePush {
            return retrofit.create(ApiInterfacePush::class.java)
        }
    }

    @POST("v1/projects/bachelor-app-98790/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun sendPush(
        @Body message: NotificationData,
        @Header("Authorization") accessToken: String = "Bearer ${AccessToken.accessToken}"
    ): NetworkResponse<Notification, ErrorResponse>



}