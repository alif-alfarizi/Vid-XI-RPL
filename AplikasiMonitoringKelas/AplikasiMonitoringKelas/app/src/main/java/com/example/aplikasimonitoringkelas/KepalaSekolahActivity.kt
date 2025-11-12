package com.example.aplikasimonitoringkelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import androidx.compose.ui.tooling.preview.Preview

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KepalaSekolahScreen()
            }
        }
    }
}

@Composable
fun KepalaSekolahScreen() {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem("Jadwal Pelajaran", "jadwal", Icons.Default.DateRange),
        BottomNavItem("Kelas Kosong", "kelas_kosong", Icons.Default.MeetingRoom),
        BottomNavItem("List", "list", Icons.Default.List)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController, navItems) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "jadwal",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("jadwal") { JadwalPelajaranScreen(role = "Kepala Sekolah") }
            composable("kelas_kosong") { KelasKosongScreen() }
            composable("list") { ListScreen(role = "Kepala Sekolah") }
        }
    }
}

@Composable
fun KelasKosongScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kelas Kosong",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Halaman ini menampilkan daftar kelas kosong.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KepalaSekolahScreenPreview() {
    AplikasiMonitoringKelasTheme {
        KepalaSekolahScreen()
    }
}