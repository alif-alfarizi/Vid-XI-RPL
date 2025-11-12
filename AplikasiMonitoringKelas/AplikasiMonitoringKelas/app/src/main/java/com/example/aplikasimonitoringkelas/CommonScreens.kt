package com.example.aplikasimonitoringkelas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

@Composable
fun JadwalPelajaranScreen(role: String = "Siswa") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (role) {
                "Siswa" -> "Halaman ini menampilkan jadwal pelajaran siswa."
                "Kurikulum" -> "Halaman ini menampilkan jadwal pelajaran untuk kurikulum."
                "Kepala Sekolah" -> "Halaman ini menampilkan jadwal pelajaran untuk kepala sekolah."
                "Admin" -> "Halaman ini menampilkan jadwal pelajaran untuk admin."
                else -> "Halaman ini menampilkan jadwal pelajaran."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ListScreen(role: String = "Siswa") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "List",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (role) {
                "Siswa" -> "Halaman ini menampilkan daftar data siswa."
                "Kurikulum" -> "Halaman ini menampilkan daftar data kurikulum."
                "Kepala Sekolah" -> "Halaman ini menampilkan daftar data untuk kepala sekolah."
                "Admin" -> "Halaman ini menampilkan daftar data untuk admin."
                else -> "Halaman ini menampilkan daftar data."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JadwalPelajaranScreenPreview() {
    AplikasiMonitoringKelasTheme {
        JadwalPelajaranScreen(role = "Admin")
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    AplikasiMonitoringKelasTheme {
        ListScreen(role = "Admin")
    }
}