package com.example.aplikasimonitoringkelas3.data.repository

import com.example.aplikasimonitoringkelas3.*
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KepalaSekolahRepository {
    
    private val apiService = RetrofitClient.apiService
    
    // Dashboard Executive
    suspend fun getDashboard(): Result<KepsekDashboardData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getKepsekDashboard()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception("Gagal memuat dashboard: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Tren Mingguan
    suspend fun getTrenMingguan(): Result<TrenMingguanData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrenMingguan()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception("Gagal memuat tren mingguan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Tren Bulanan
    suspend fun getTrenBulanan(tahun: Int? = null): Result<TrenBulananData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTrenBulanan(tahun)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception("Gagal memuat tren bulanan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Ranking Guru
    suspend fun getRankingGuru(
        bulan: Int? = null,
        tahun: Int? = null,
        limit: Int? = null
    ): Result<RankingGuruData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRankingGuru(bulan, tahun, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception("Gagal memuat ranking guru: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Laporan
    suspend fun getLaporan(
        bulan: Int? = null,
        tahun: Int? = null
    ): Result<LaporanKepsekData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLaporanKepsek(bulan, tahun)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Data tidak ditemukan"))
            } else {
                Result.failure(Exception("Gagal memuat laporan: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
