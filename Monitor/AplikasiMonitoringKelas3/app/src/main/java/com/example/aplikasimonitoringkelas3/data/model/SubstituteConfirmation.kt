package com.example.aplikasimonitoringkelas3.data.model

import com.google.gson.annotations.SerializedName

// ==================== SUBSTITUTE CONFIRMATION MODELS ====================

// Response untuk list konfirmasi guru pengganti (untuk siswa)
data class SubstituteConfirmationListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<SubstituteConfirmationItem>?
)

// Item guru pengganti dengan status konfirmasi
data class SubstituteConfirmationItem(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("jadwal")
    val jadwal: JadwalInfo?,

    @SerializedName("guru_asli")
    val guruAsli: GuruInfo?,

    @SerializedName("guru_pengganti")
    val guruPengganti: GuruInfo?,

    @SerializedName("alasan")
    val alasan: String,

    @SerializedName("keterangan")
    val keterangan: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("sudah_dikonfirmasi")
    val sudahDikonfirmasi: Boolean,

    @SerializedName("waktu_konfirmasi")
    val waktuKonfirmasi: String?,

    @SerializedName("jumlah_konfirmasi")
    val jumlahKonfirmasi: Int
)

data class JadwalInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("hari")
    val hari: String,

    @SerializedName("jam_mulai")
    val jamMulai: String,

    @SerializedName("jam_selesai")
    val jamSelesai: String,

    @SerializedName("mata_pelajaran")
    val mataPelajaran: String?,

    @SerializedName("kelas")
    val kelas: KelasInfo?
)

data class KelasInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama")
    val nama: String
)

data class GuruInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama")
    val nama: String
)

// Request untuk konfirmasi guru pengganti sudah masuk
data class SubstituteConfirmationRequest(
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int,
    
    @SerializedName("keterangan")
    val keterangan: String? = null
)

// Response untuk create konfirmasi
data class SubstituteConfirmationResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ConfirmationData?
)

data class ConfirmationData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("confirmed_at")
    val confirmedAt: String
)

// Response untuk detail konfirmasi (siapa saja yang sudah konfirmasi)
data class SubstituteConfirmationDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ConfirmationDetailData?
)

data class ConfirmationDetailData(
    @SerializedName("guru_pengganti")
    val guruPengganti: GuruPenggantiSummary,

    @SerializedName("confirmations")
    val confirmations: List<ConfirmationUser>,

    @SerializedName("total_konfirmasi")
    val totalKonfirmasi: Int
)

data class GuruPenggantiSummary(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("guru_asli")
    val guruAsli: String?,

    @SerializedName("guru_pengganti")
    val guruPengganti: String?,

    @SerializedName("kelas")
    val kelas: String?
)

data class ConfirmationUser(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user")
    val user: UserInfo,

    @SerializedName("confirmed_at")
    val confirmedAt: String
)

data class UserInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)
