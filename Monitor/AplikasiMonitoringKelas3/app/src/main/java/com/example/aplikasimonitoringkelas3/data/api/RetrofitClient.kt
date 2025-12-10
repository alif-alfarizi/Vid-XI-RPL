package com.example.aplikasimonitoringkelas3.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // ===================== PILIH SALAH SATU BASE_URL =====================
    // 
    // OPSI 1: Jika menggunakan EMULATOR Android Studio
    // private const val BASE_URL = "http://10.0.2.2:8000/api/"
    //
    // OPSI 2: Jika menggunakan DEVICE FISIK + ADB USB (RECOMMENDED)
    // Jalankan: adb reverse tcp:8000 tcp:8000
    // private const val BASE_URL = "http://localhost:8000/api/"
    //
    // OPSI 3: Jika menggunakan WIFI (HP dan Laptop di network yang sama)
    // Ganti dengan IP komputer Anda
    // private const val BASE_URL = "http://192.168.40.3:8000/api/"
    //
    // =====================================================================
    
    // Gunakan IP Komputer untuk HP Fisik via WiFi
    private const val BASE_URL = "http://192.168.40.21:8000/api/"
    
    private var authToken: String? = null
    
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    fun clearToken() {
        authToken = null
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Add authorization header if token exists
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        // Add accept header
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")
        
        chain.proceed(requestBuilder.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
