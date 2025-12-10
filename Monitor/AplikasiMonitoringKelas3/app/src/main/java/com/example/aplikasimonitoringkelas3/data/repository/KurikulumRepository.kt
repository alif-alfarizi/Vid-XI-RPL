package com.example.aplikasimonitoringkelas3.data.repository

import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import retrofit2.Response

class KurikulumRepository {
    private val apiService = RetrofitClient.apiService

    // Dashboard
    suspend fun getDashboard(): Response<KurikulumDashboardResponse> {
        return apiService.getKurikulumDashboard()
    }
    
    // Rekap Kehadiran
    suspend fun getRekapKehadiran(
        tanggalMulai: String? = null,
        tanggalSelesai: String? = null,
        guruId: Int? = null
    ): Response<RekapKehadiranResponse> {
        return apiService.getRekapKehadiran(tanggalMulai, tanggalSelesai, guruId)
    }
    
    // List Guru
    suspend fun getListGuru(): Response<ListGuruResponse> {
        return apiService.getListGuru()
    }
    
    // Statistik Guru Detail
    suspend fun getStatistikGuru(guruId: Int): Response<StatistikGuruResponse> {
        return apiService.getStatistikGuru(guruId)
    }
    
    // List Kelas
    suspend fun getListKelas(): Response<ListKelasResponse> {
        return apiService.getListKelas()
    }
    
    // List Jadwal dengan Filter
    suspend fun getJadwal(
        hari: String? = null,
        kelasId: Int? = null,
        guruId: Int? = null
    ): Response<ListJadwalResponse> {
        return apiService.getKurikulumJadwal(hari, kelasId, guruId)
    }
    
    // Laporan Harian
    suspend fun getLaporanHarian(tanggal: String? = null): Response<LaporanHarianResponse> {
        return apiService.getLaporanHarian(tanggal)
    }
    
    // ==================== GURU PENGGANTI ====================
    
    // Get guru yang absen hari ini
    suspend fun getGuruAbsen(tanggal: String? = null): Response<GuruAbsenResponse> {
        return apiService.getGuruAbsen(tanggal)
    }
    
    // Get guru yang tersedia untuk menggantikan
    suspend fun getGuruTersedia(tanggal: String? = null, jadwalId: Int): Response<GuruTersediaResponse> {
        return apiService.getGuruTersedia(tanggal, jadwalId)
    }
    
    // Get list guru pengganti
    suspend fun getGuruPengganti(tanggal: String? = null, status: String? = null): Response<GuruPenggantiListResponse> {
        return apiService.getGuruPengganti(tanggal, status)
    }
    
    // Create guru pengganti
    suspend fun createGuruPengganti(request: GuruPenggantiRequest): Response<GuruPenggantiResponse> {
        return apiService.createGuruPengganti(request)
    }
    
    // Update status guru pengganti
    suspend fun updateStatusGuruPengganti(id: Int, status: String): Response<GuruPenggantiResponse> {
        return apiService.updateStatusGuruPengganti(id, mapOf("status" to status))
    }
    
    // Delete guru pengganti
    suspend fun deleteGuruPengganti(id: Int): Response<ApiResponse<Any>> {
        return apiService.deleteGuruPengganti(id)
    }
}
