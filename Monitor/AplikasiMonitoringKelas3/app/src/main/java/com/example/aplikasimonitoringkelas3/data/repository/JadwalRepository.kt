package com.example.aplikasimonitoringkelas3.data.repository

import android.util.Log
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.Jadwal
import com.example.aplikasimonitoringkelas3.data.model.JadwalRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JadwalRepository {
    
    private val apiService = RetrofitClient.apiService
    
    suspend fun getJadwals(): Result<List<Jadwal>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getJadwals()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal mengambil data jadwal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("JadwalRepository", "Get jadwals error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun createJadwal(jadwal: JadwalRequest): Result<Jadwal> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createJadwal(jadwal)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal membuat jadwal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("JadwalRepository", "Create jadwal error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun updateJadwal(id: Int, jadwal: JadwalRequest): Result<Jadwal> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateJadwal(id, jadwal)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal mengupdate jadwal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("JadwalRepository", "Update jadwal error", e)
                Result.failure(e)
            }
        }
    }
    
    suspend fun deleteJadwal(id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteJadwal(id)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(body?.message ?: "Gagal menghapus jadwal"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("JadwalRepository", "Delete jadwal error", e)
                Result.failure(e)
            }
        }
    }
}
