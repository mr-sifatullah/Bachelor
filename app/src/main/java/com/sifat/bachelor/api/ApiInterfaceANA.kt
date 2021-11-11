package com.sifat.bachelor.api


import com.haroldadmin.cnradapter.NetworkResponse
import com.sifat.bachelor.api.model.LoginResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterfaceANA {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceANA {
            return retrofit.create(ApiInterfaceANA::class.java)
        }
    }

    @Headers("key: AIzaSyDqJLrcRdvJjkaZMZWCvbQyC3gGnWJog4M")
    @GET("13cuJeC78SpkzDuz5p1ykgCdOgThcyzxlzfQJm5UiblE/values/sign_in")
    suspend fun userLogin(): NetworkResponse<LoginResponse, ErrorResponse>



}