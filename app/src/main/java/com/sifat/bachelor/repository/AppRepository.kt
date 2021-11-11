package com.sifat.bachelor.repository

import com.sifat.bachelor.api.ApiInterfaceANA


class AppRepository(
    private val apiInterfaceANA: ApiInterfaceANA
) {
    suspend fun userLogin() = apiInterfaceANA.userLogin()

}