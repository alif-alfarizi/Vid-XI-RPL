package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class AdminActivity : ComponentActivity() {
    // 1. Dapatkan instance ViewModel yang terikat pada siklus hidup Activity
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                // 2. Berikan ViewModel ke Composable utama
                AdminScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: ScheduleViewModel) {
    val context = LocalContext.current
    // 3. Ambil daftar jadwal langsung dari ViewModel.
    // Daftar ini bersifat reaktif karena berasal dari mutableStateListOf di Repository.
    val scheduleList = viewModel.schedules
    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ScheduleItem?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Halaman Admin") },
                actions = {
                    IconButton(onClick = {
                        // Kembali ke MainActivity (Layar Login)
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                itemToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(scheduleList) { item ->
                    ScheduleEditCard(
                        item = item,
                        onEdit = {
                            itemToEdit = it
                            showDialog = true
                        },
                        onDelete = {
                            // 4. Panggil fungsi delete dari ViewModel
                            viewModel.deleteSchedule(it)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddEditScheduleDialog(
            item = itemToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { updatedItem ->
                if (itemToEdit == null) {
                    // 5. Panggil fungsi add dari ViewModel
                    viewModel.addSchedule(updatedItem)
                } else {
                    // 6. Panggil fungsi update dari ViewModel
                    viewModel.updateSchedule(updatedItem)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ScheduleEditCard(item: ScheduleItem, onEdit: (ScheduleItem) -> Unit, onDelete: (ScheduleItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.mataPelajaran, style = MaterialTheme.typography.titleLarge)
            Text("${item.hari} - ${item.jam}")
            Text("Kelas: ${item.kelas}")
            Text("Guru: ${item.guru}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onEdit(item) }) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDelete(item) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            }
        }
    }
}

@Composable
fun AddEditScheduleDialog(
    item: ScheduleItem?,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleItem) -> Unit
) {
    var hari by remember { mutableStateOf(item?.hari ?: "") }
    var jam by remember { mutableStateOf(item?.jam ?: "") }
    var mataPelajaran by remember { mutableStateOf(item?.mataPelajaran ?: "") }
    var kelas by remember { mutableStateOf(item?.kelas ?: "") }
    var guru by remember { mutableStateOf(item?.guru ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Tambah Jadwal" else "Edit Jadwal") },
        text = {
            Column {
                TextField(value = hari, onValueChange = { hari = it }, label = { Text("Hari") })
                TextField(value = jam, onValueChange = { jam = it }, label = { Text("Jam") })
                TextField(value = mataPelajaran, onValueChange = { mataPelajaran = it }, label = { Text("Mata Pelajaran") })
                TextField(value = kelas, onValueChange = { kelas = it }, label = { Text("Kelas") })
                TextField(value = guru, onValueChange = { guru = it }, label = { Text("Guru") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    ScheduleItem(
                        id = item?.id ?: 0, // ID akan di-generate oleh repository saat item baru
                        hari = hari,
                        jam = jam,
                        mataPelajaran = mataPelajaran,
                        kelas = kelas,
                        guru = guru
                    )
                )
            }) { Text("Simpan") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AplikasiMonitoringKelasTheme {
        // Untuk preview, kita bisa membuat instance ViewModel palsu jika diperlukan,
        // tapi untuk kasus ini, repository sudah cukup.
        AdminScreen(viewModel = ScheduleViewModel())
    }
}
