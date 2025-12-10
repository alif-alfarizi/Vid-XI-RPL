package com.example.aplikasimonitoringkelas3.data.model

import com.google.gson.annotations.SerializedName

data class Jadwal(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("kelas_id")
    val kelasId: Int? = null,
    
    @SerializedName("guru_id")
    val guruId: Int? = null,
    
    @SerializedName("mata_pelajaran")
    val mataPelajaran: String,
    
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("jam_mulai")
    val jamMulai: String,
    
    @SerializedName("jam_selesai")
    val jamSelesai: String,
    
    @SerializedName("ruangan")
    val ruangan: String? = null,
    
    @SerializedName("kelas")
    val kelas: Kelas? = null,
    
    @SerializedName("guru")
    val guru: Guru? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class JadwalRequest(
    @SerializedName("kelas")
    val kelas: String,
    
    @SerializedName("guru")
    val guru: String,
    
    @SerializedName("mata_pelajaran")
    val mataPelajaran: String,
    
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("jam_ke")
    val jamKe: String,
    
    @SerializedName("ruangan")
    val ruangan: String? = null
)

data class Kelas(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nama_kelas")
    val namaKelas: String
)

data class Guru(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nama")
    val nama: String,
    
    @SerializedName("nip")
    val nip: String? = null,
    
    @SerializedName("mata_pelajaran")
    val mataPelajaran: String? = null
)

// Response untuk jadwal siswa
data class SiswaJadwalResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: SiswaJadwalData?
)

data class SiswaJadwalData(
    @SerializedName("kelas")
    val kelas: Kelas?,
    
    @SerializedName("jadwal")
    val jadwal: Map<String, List<Jadwal>>
)

data class SiswaJadwalHariIniResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: SiswaJadwalHariIniData?
)

data class SiswaJadwalHariIniData(
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("kelas")
    val kelas: Kelas?,
    
    @SerializedName("jadwal")
    val jadwal: List<Jadwal>
)

// Model untuk Kehadiran Guru
data class TeacherAttendance(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("guru_id")
    val guruId: Int,
    
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("jam_masuk")
    val jamMasuk: String?,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("guru")
    val guru: Guru?
)

data class TeacherAttendanceRequest(
    @SerializedName("guru_id")
    val guruId: Int,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("keterangan")
    val keterangan: String? = null
)

data class TeacherAttendanceTodayResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: TeacherAttendanceTodayData?
)

data class TeacherAttendanceTodayData(
    @SerializedName("tanggal")
    val tanggal: String,
    
    @SerializedName("tanggal_formatted")
    val tanggalFormatted: String,
    
    @SerializedName("summary")
    val summary: AttendanceSummary,
    
    @SerializedName("guru_status")
    val guruStatus: List<GuruAttendanceStatus>,
    
    @SerializedName("attendances")
    val attendances: List<TeacherAttendance>
)

data class AttendanceSummary(
    @SerializedName("total_guru")
    val totalGuru: Int,
    
    @SerializedName("hadir")
    val hadir: Int,
    
    @SerializedName("terlambat")
    val terlambat: Int,
    
    @SerializedName("tidak_hadir")
    val tidakHadir: Int,
    
    @SerializedName("izin")
    val izin: Int,
    
    @SerializedName("belum_input")
    val belumInput: Int
)

data class GuruAttendanceStatus(
    @SerializedName("guru")
    val guru: Guru,
    
    @SerializedName("has_attendance")
    val hasAttendance: Boolean,
    
    @SerializedName("attendance")
    val attendance: TeacherAttendance?,
    
    @SerializedName("jadwal_mengajar")
    val jadwalMengajar: List<Jadwal>? = null,
    
    @SerializedName("digantikan")
    val digantikan: GuruPenggantiInfo? = null,
    
    @SerializedName("menggantikan")
    val menggantikan: GuruPenggantiInfo? = null
)

data class GuruPenggantiInfo(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("alasan")
    val alasan: String?,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("guru_pengganti")
    val guruPengganti: Guru? = null,
    
    @SerializedName("guru_asli")
    val guruAsli: Guru? = null,
    
    @SerializedName("jadwal")
    val jadwal: Jadwal? = null
)
