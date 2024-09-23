package com.sifat.bachelor.api


import com.haroldadmin.cnradapter.NetworkResponse
import com.sifat.bachelor.api.model.LoginResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterfaceANA {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceANA {
            return retrofit.create(ApiInterfaceANA::class.java)
        }
    }

    @GET("13cuJeC78SpkzDuz5p1ykgCdOgThcyzxlzfQJm5UiblE/values/sign_in")
    suspend fun userLogin(@Query("key") key: String): NetworkResponse<LoginResponse, ErrorResponse>

    @GET("13cuJeC78SpkzDuz5p1ykgCdOgThcyzxlzfQJm5UiblE/values/notice")
    suspend fun getUserNotice(@Query("key") key: String): NetworkResponse<LoginResponse, ErrorResponse>

    @GET("13cuJeC78SpkzDuz5p1ykgCdOgThcyzxlzfQJm5UiblE/values/home_rent")
    suspend fun getUserHomeRentInfo(@Query("key") key: String): NetworkResponse<LoginResponse, ErrorResponse>

    @GET("13cuJeC78SpkzDuz5p1ykgCdOgThcyzxlzfQJm5UiblE/values/meal")
    suspend fun getUserMealInfo(@Query("key") key: String): NetworkResponse<LoginResponse, ErrorResponse>



}