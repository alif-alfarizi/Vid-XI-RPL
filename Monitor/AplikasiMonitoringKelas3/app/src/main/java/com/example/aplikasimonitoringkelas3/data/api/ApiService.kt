package com.example.aplikasimonitoringkelas3.data.api

import com.example.aplikasimonitoringkelas3.data.model.*
import com.example.aplikasimonitoringkelas3.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("register")
    suspend fun register(@Body user: User): Response<LoginResponse>
    
    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Any>>
    
    // Users
    @GET("users")
    suspend fun getUsers(): Response<ApiResponse<List<User>>>
    
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<ApiResponse<User>>
    
    @POST("users")
    suspend fun createUser(@Body user: User): Response<ApiResponse<User>>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<ApiResponse<User>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<Any>>
    
    // Jadwal
    @GET("jadwals")
    suspend fun getJadwals(): Response<ApiResponse<List<Jadwal>>>
    
    @GET("jadwals/{id}")
    suspend fun getJadwal(@Path("id") id: Int): Response<ApiResponse<Jadwal>>
    
    @POST("jadwals")
    suspend fun createJadwal(@Body jadwal: JadwalRequest): Response<ApiResponse<Jadwal>>
    
    @PUT("jadwals/{id}")
    suspend fun updateJadwal(@Path("id") id: Int, @Body jadwal: JadwalRequest): Response<ApiResponse<Jadwal>>
    
    @DELETE("jadwals/{id}")
    suspend fun deleteJadwal(@Path("id") id: Int): Response<ApiResponse<Any>>
    
    // Siswa - Jadwal berdasarkan kelas
    @GET("siswa/jadwal")
    suspend fun getSiswaJadwal(): Response<SiswaJadwalResponse>
    
    @GET("siswa/jadwal/hari-ini")
    suspend fun getSiswaJadwalHariIni(): Response<SiswaJadwalHariIniResponse>
    
    // Teacher Attendance - Kehadiran Guru
    @GET("gurus")
    suspend fun getGurus(): Response<ApiResponse<List<Guru>>>
    
    @GET("teacher-attendance/today")
    suspend fun getTeacherAttendanceToday(): Response<TeacherAttendanceTodayResponse>
    
    @POST("teacher-attendance")
    suspend fun createTeacherAttendance(@Body request: TeacherAttendanceRequest): Response<ApiResponse<TeacherAttendance>>
    
    // Kurikulum - Dashboard dan Manajemen
    @GET("kurikulum/dashboard")
    suspend fun getKurikulumDashboard(): Response<KurikulumDashboardResponse>
    
    @GET("kurikulum/rekap-kehadiran")
    suspend fun getRekapKehadiran(
        @Query("tanggal_mulai") tanggalMulai: String? = null,
        @Query("tanggal_selesai") tanggalSelesai: String? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<RekapKehadiranResponse>
    
    @GET("kurikulum/guru")
    suspend fun getListGuru(): Response<ListGuruResponse>
    
    @GET("kurikulum/guru/{id}/statistik")
    suspend fun getStatistikGuru(@Path("id") guruId: Int): Response<StatistikGuruResponse>
    
    @GET("kurikulum/kelas")
    suspend fun getListKelas(): Response<ListKelasResponse>
    
    @GET("kurikulum/jadwal")
    suspend fun getKurikulumJadwal(
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null,
        @Query("guru_id") guruId: Int? = null
    ): Response<ListJadwalResponse>
    
    @GET("kurikulum/laporan-harian")
    suspend fun getLaporanHarian(@Query("tanggal") tanggal: String? = null): Response<LaporanHarianResponse>

    // Kurikulum - Guru Pengganti
    @GET("kurikulum/guru-absen")
    suspend fun getGuruAbsen(@Query("tanggal") tanggal: String? = null): Response<GuruAbsenResponse>
    
    @GET("kurikulum/guru-tersedia")
    suspend fun getGuruTersedia(
        @Query("tanggal") tanggal: String? = null,
        @Query("jadwal_id") jadwalId: Int
    ): Response<GuruTersediaResponse>
    
    @GET("kurikulum/guru-pengganti")
    suspend fun getGuruPengganti(
        @Query("tanggal") tanggal: String? = null,
        @Query("status") status: String? = null
    ): Response<GuruPenggantiListResponse>
    
    @POST("kurikulum/guru-pengganti")
    suspend fun createGuruPengganti(@Body request: GuruPenggantiRequest): Response<GuruPenggantiResponse>
    
    @PATCH("kurikulum/guru-pengganti/{id}/status")
    suspend fun updateStatusGuruPengganti(
        @Path("id") id: Int,
        @Body request: Map<String, String>
    ): Response<GuruPenggantiResponse>
    
    @DELETE("kurikulum/guru-pengganti/{id}")
    suspend fun deleteGuruPengganti(@Path("id") id: Int): Response<ApiResponse<Any>>

    // ==================== KEPALA SEKOLAH ====================
    
    // Dashboard Executive
    @GET("kepala-sekolah/dashboard")
    suspend fun getKepsekDashboard(): Response<KepsekDashboardResponse>
    
    // Tren Mingguan
    @GET("kepala-sekolah/tren-mingguan")
    suspend fun getTrenMingguan(): Response<TrenMingguanResponse>
    
    // Tren Bulanan
    @GET("kepala-sekolah/tren-bulanan")
    suspend fun getTrenBulanan(@Query("tahun") tahun: Int? = null): Response<TrenBulananResponse>
    
    // Ranking Guru
    @GET("kepala-sekolah/ranking-guru")
    suspend fun getRankingGuru(
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<RankingGuruResponse>
    
    // Laporan
    @GET("kepala-sekolah/laporan")
    suspend fun getLaporanKepsek(
        @Query("bulan") bulan: Int? = null,
        @Query("tahun") tahun: Int? = null
    ): Response<LaporanKepsekResponse>

    // ==================== SUBSTITUTE CONFIRMATION ====================
    
    // Daftar guru pengganti hari ini untuk kelas siswa dengan status konfirmasi
    @GET("substitute-confirmations")
    suspend fun getSubstituteConfirmations(): Response<SubstituteConfirmationListResponse>
    
    // Siswa konfirmasi bahwa guru pengganti sudah masuk kelas
    @POST("substitute-confirmations")
    suspend fun confirmSubstitute(@Body request: SubstituteConfirmationRequest): Response<SubstituteConfirmationResponse>
    
    // Daftar konfirmasi untuk guru pengganti tertentu
    @GET("substitute-confirmations/{guruPenggantiId}")
    suspend fun getSubstituteConfirmationDetail(@Path("guruPenggantiId") guruPenggantiId: Int): Response<SubstituteConfirmationDetailResponse>
}
