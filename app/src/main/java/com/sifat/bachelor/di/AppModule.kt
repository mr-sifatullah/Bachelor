package com.sifat.bachelor.di

import com.sifat.bachelor.AppConstant
import com.sifat.bachelor.api.ApiInterfaceANA
import com.sifat.bachelor.api.RetrofitUtils.createCache
import com.sifat.bachelor.api.RetrofitUtils.createOkHttpClient
import com.sifat.bachelor.api.RetrofitUtils.getGson
import com.sifat.bachelor.api.RetrofitUtils.retrofitInstance
import com.sifat.bachelor.home.HomeActivityViewModel
import com.sifat.bachelor.home.HomeViewModel
import com.sifat.bachelor.login.LoginViewModel
import com.sifat.bachelor.repository.AppRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single { getGson() }
    single { createCache(get()) }
    single { createOkHttpClient(get()) }
    single(named("normal")) { createOkHttpClient(get()) }

    single(named("api")) { retrofitInstance(AppConstant.BASE_URL, get(), get()) }

    single { ApiInterfaceANA(get(named("api"))) }

    single { AppRepository(get()) }

    single { HomeActivityViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}