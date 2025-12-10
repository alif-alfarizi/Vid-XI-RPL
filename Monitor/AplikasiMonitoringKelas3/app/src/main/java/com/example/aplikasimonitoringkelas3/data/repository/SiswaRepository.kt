package com.example.aplikasimonitoringkelas3.data.repository

import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import retrofit2.Response

class SiswaRepository {
    private val apiService = RetrofitClient.apiService

    // Get jadwal based on student's class
    suspend fun getJadwalByKelas(): Response<SiswaJadwalResponse> {
        return apiService.getSiswaJadwal()
    }
    
    // Get today's jadwal for student's class
    suspend fun getJadwalHariIni(): Response<SiswaJadwalHariIniResponse> {
        return apiService.getSiswaJadwalHariIni()
    }
    
    // Get today's teacher attendance
    suspend fun getTodayAttendance(): Response<TeacherAttendanceTodayResponse> {
        return apiService.getTeacherAttendanceToday()
    }
    
    // Create teacher attendance record
    suspend fun createTeacherAttendance(request: TeacherAttendanceRequest): Response<ApiResponse<TeacherAttendance>> {
        return apiService.createTeacherAttendance(request)
    }
    
    // Get all teachers
    suspend fun getGurus(): Response<ApiResponse<List<Guru>>> {
        return apiService.getGurus()
    }

    // ==================== SUBSTITUTE CONFIRMATION ====================
    
    // Get substitute teachers for today with confirmation status
    suspend fun getSubstituteConfirmations(): Response<SubstituteConfirmationListResponse> {
        return apiService.getSubstituteConfirmations()
    }
    
    // Confirm that substitute teacher has entered the class
    suspend fun confirmSubstitute(guruPenggantiId: Int, keterangan: String? = null): Response<SubstituteConfirmationResponse> {
        return apiService.confirmSubstitute(SubstituteConfirmationRequest(guruPenggantiId, keterangan))
    }
    
    // Get confirmation details for a specific substitute teacher
    suspend fun getSubstituteConfirmationDetail(guruPenggantiId: Int): Response<SubstituteConfirmationDetailResponse> {
        return apiService.getSubstituteConfirmationDetail(guruPenggantiId)
    }
}
