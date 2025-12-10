package com.example.aplikasimonitoringkelas3

import com.google.gson.annotations.SerializedName

// ==================== DASHBOARD ====================

data class KepsekDashboardResponse(
    val success: Boolean,
    val data: KepsekDashboardData?
)

data class KepsekDashboardData(
    val summary: KepsekSummary,
    @SerializedName("kehadiran_hari_ini") val kehadiranHariIni: KehadiranHariIniKepsek,
    @SerializedName("performa_bulanan") val performaBulanan: PerformaBulanan,
    val tanggal: String,
    val hari: String
)

data class KepsekSummary(
    @SerializedName("total_guru") val totalGuru: Int,
    @SerializedName("total_kelas") val totalKelas: Int,
    @SerializedName("total_jadwal") val totalJadwal: Int,
    @SerializedName("jadwal_hari_ini") val jadwalHariIni: Int
)

data class KehadiranHariIniKepsek(
    val hadir: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    val izin: Int,
    val total: Int,
    @SerializedName("persentase_hadir") val persentaseHadir: Double
)

data class PerformaBulanan(
    @SerializedName("bulan_ini") val bulanIni: BulanData,
    @SerializedName("bulan_lalu") val bulanLalu: BulanLaluData,
    val selisih: Double,
    val trend: String
)

data class BulanData(
    val bulan: String,
    val persentase: Double,
    @SerializedName("total_hadir") val totalHadir: Int,
    @SerializedName("total_kehadiran") val totalKehadiran: Int
)

data class BulanLaluData(
    val bulan: String,
    val persentase: Double
)

// ==================== TREN MINGGUAN ====================

data class TrenMingguanResponse(
    val success: Boolean,
    val data: TrenMingguanData?
)

data class TrenMingguanData(
    val tren: List<TrenHarian>,
    @SerializedName("rata_rata") val rataRata: Double,
    val periode: PeriodeTren
)

data class TrenHarian(
    val tanggal: String,
    val hari: String,
    @SerializedName("hari_singkat") val hariSingkat: String,
    val hadir: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    val izin: Int,
    val total: Int,
    val persentase: Double
)

data class PeriodeTren(
    val mulai: String,
    val selesai: String
)

// ==================== TREN BULANAN ====================

data class TrenBulananResponse(
    val success: Boolean,
    val data: TrenBulananData?
)

data class TrenBulananData(
    val tahun: Int,
    val tren: List<TrenBulanan>
)

data class TrenBulanan(
    val bulan: Int,
    @SerializedName("nama_bulan") val namaBulan: String,
    @SerializedName("nama_bulan_singkat") val namaBulanSingkat: String,
    val hadir: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    val izin: Int,
    val total: Int,
    val persentase: Double
)

// ==================== RANKING GURU ====================

data class RankingGuruResponse(
    val success: Boolean,
    val data: RankingGuruData?
)

data class RankingGuruData(
    val periode: PeriodeRanking,
    val ranking: List<GuruRanking>,
    @SerializedName("top_performers") val topPerformers: List<GuruRanking>,
    @SerializedName("need_attention") val needAttention: List<GuruRanking>,
    @SerializedName("total_guru") val totalGuru: Int
)

data class PeriodeRanking(
    val bulan: String,
    val tahun: Int
)

data class GuruRanking(
    @SerializedName("guru_id") val guruId: Int,
    val nama: String,
    val nip: String?,
    @SerializedName("mata_pelajaran") val mataPelajaran: String?,
    val hadir: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    val izin: Int,
    val total: Int,
    val persentase: Double,
    val ranking: Int
)

// ==================== LAPORAN ====================

data class LaporanKepsekResponse(
    val success: Boolean,
    val data: LaporanKepsekData?
)

data class LaporanKepsekData(
    val periode: PeriodeLaporan,
    val ringkasan: RingkasanLaporan,
    @SerializedName("kehadiran_per_hari") val kehadiranPerHari: List<KehadiranPerHari>,
    @SerializedName("guru_terbaik") val guruTerbaik: List<GuruTerbaikLaporan>,
    @SerializedName("guru_perhatian") val guruPerhatian: List<GuruPerhatianLaporan>
)

data class PeriodeLaporan(
    val bulan: String,
    @SerializedName("bulan_angka") val bulanAngka: Int,
    val tahun: Int
)

data class RingkasanLaporan(
    @SerializedName("total_kehadiran") val totalKehadiran: Int,
    val hadir: Int,
    @SerializedName("tidak_hadir") val tidakHadir: Int,
    val izin: Int,
    @SerializedName("persentase_hadir") val persentaseHadir: Double
)

data class KehadiranPerHari(
    val hari: String,
    val total: Int,
    val hadir: Int,
    val persentase: Double
)

data class GuruTerbaikLaporan(
    val id: Int,
    val nama: String,
    @SerializedName("total_hadir") val totalHadir: Int
)

data class GuruPerhatianLaporan(
    val id: Int,
    val nama: String,
    @SerializedName("total_tidak_hadir") val totalTidakHadir: Int
)
