package com.example.budgetbuddy.modules

import com.example.budgetbuddy.http.FcmAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * Modulo que sirve para implementar inyeccion de dependencias a los ViewModel.
 * En este caso para inyector el servicio de API para realizar llamadas al backend local
 * Uso de la inyección de dependencias consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Dependency Injection with Dagger, Hilt, and Koin
 * @author Armando Guzmán
 * */
@Module
@InstallIn(SingletonComponent::class)
class RetrofitModules {
    /**
     * Método que provee la url base
     * @return Url base para el servicio
     * */
    @Provides
    fun provideBaseUrl(): String = "http://10.0.2.2:8080/"

    /**
     * Método que provee el objeto de [Retrofit] construido con la url base y el converter
     * @param baseUrl Url base inyectada mediante el método anterior
     * @return Objeto de [Retrofit] construido
     * */
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =  Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    /**
     * Método que provee el objeto de [Retrofit] aplicando los métodos definidos en la interfaz
     * [FcmAPI]
     * @param retrofit Objeto de [Retrofit] construido, inyectado por el método anterior
     * @return El objeto de [FcmAPI] con los métodos definidos en la interfaz listos para
     * usar.
     * */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit):FcmAPI = retrofit.create(FcmAPI::class.java)
}