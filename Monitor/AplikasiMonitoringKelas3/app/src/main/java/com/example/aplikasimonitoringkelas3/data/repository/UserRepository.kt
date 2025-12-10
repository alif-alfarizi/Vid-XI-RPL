package com.example.aplikasimonitoringkelas3.data.repository

import android.util.Log
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    
    private val apiService = RetrofitClient.apiService
    
    suspend fun login(email: String, password: String): Result<LoginData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        // Save token
                        RetrofitClient.setAuthToken(body.data.token)
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Login gagal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Login error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun register(user: User): Result<LoginData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(user)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        // Save token
                        RetrofitClient.setAuthToken(body.data.token)
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Registrasi gagal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Register error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun getUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUsers()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal mengambil data user"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Get users error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun createUser(user: User): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createUser(user)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal membuat user"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Create user error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteUser(id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteUser(id)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal menghapus user"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Delete user error", e)
                Result.failure(e)
            }
        }
    }
}
