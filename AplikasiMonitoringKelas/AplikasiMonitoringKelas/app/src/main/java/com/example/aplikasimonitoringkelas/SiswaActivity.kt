package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                SiswaMainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaMainScreen() {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Jadwal Pelajaran", "Entri", "List")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Halaman Siswa") },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // TabRow diletakkan di bawah TopAppBar
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        icon = {
                            when (title) {
                                "Jadwal Pelajaran" -> Icon(Icons.Default.DateRange, contentDescription = title)
                                "Entri" -> Icon(Icons.Default.Edit, contentDescription = title)
                                "List" -> Icon(Icons.Default.List, contentDescription = title)
                            }
                        }
                    )
                }
            }

            // Konten sesuai tab yang dipilih
            when (selectedTabIndex) {
                0 -> JadwalPelajaranScreenSiswa()
                1 -> EntriScreen()
                2 -> ListScreen()
            }
        }
    }
}

// --- PERUBAHAN UTAMA DIMULAI DI SINI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPelajaranScreenSiswa() {
    // 1. Definisikan daftar kelas yang tersedia dan state untuk dropdown
    val availableClasses = listOf("X RPL", "XI RPL", "XII RPL")
    var selectedClass by remember { mutableStateOf(availableClasses[0]) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Ambil seluruh daftar jadwal dari repository.
    val allSchedules = ScheduleRepository.scheduleList

    // Gunakan `remember` dan `derivedStateOf` untuk membuat state turunan yang reaktif.
    val filteredSchedules by remember(allSchedules, selectedClass) {
        derivedStateOf {
            allSchedules.filter { it.kelas == selectedClass }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2. Tambahkan Dropdown untuk memilih kelas
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "Kelas: $selectedClass",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                availableClasses.forEach { className ->
                    DropdownMenuItem(
                        text = { Text(className) },
                        onClick = {
                            selectedClass = className
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3. LazyColumn sekarang akan otomatis menampilkan jadwal sesuai `selectedClass`
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredSchedules.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada jadwal untuk kelas $selectedClass",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            } else {
                items(filteredSchedules) { item ->
                    ScheduleCard(item = item)
                }
            }
        }
    }
}

// --- Composable lain tidak perlu diubah ---

@Composable
fun ScheduleCard(item: ScheduleItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.jam,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.mataPelajaran,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kelas: ${item.kelas}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Guru: ${item.guru}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Composable
fun EntriScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Halaman Entri Data", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ListScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daftar Entri Saya", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun SiswaMainScreenPreview() {
    AplikasiMonitoringKelasTheme {
        SiswaMainScreen()
    }
}

