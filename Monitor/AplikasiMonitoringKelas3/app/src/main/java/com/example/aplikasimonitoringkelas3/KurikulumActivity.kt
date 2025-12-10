package com.example.aplikasimonitoringkelas3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas3.data.api.RetrofitClient
import com.example.aplikasimonitoringkelas3.data.model.*
import com.example.aplikasimonitoringkelas3.data.repository.KurikulumRepository
import com.example.aplikasimonitoringkelas3.ui.theme.AplikasiMonitoringKelas3Theme
import com.example.aplikasimonitoringkelas3.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiMonitoringKelas3Theme {
                KurikulumScreen(
                    onLogout = {
                        RetrofitClient.clearToken()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf(
        KurikulumTab("Dashboard", Icons.Default.Home),
        KurikulumTab("Kehadiran", Icons.Default.CheckCircle),
        KurikulumTab("Pengganti", Icons.Default.Refresh),
        KurikulumTab("Jadwal", Icons.Default.DateRange),
        KurikulumTab("Guru", Icons.Default.Person),
        KurikulumTab("Profile", Icons.Default.AccountCircle)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Surface,
                contentColor = Primary,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                tab.icon, 
                                contentDescription = tab.title,
                                modifier = Modifier.size(if (selectedTab == index) 28.dp else 24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                tab.title, 
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = Primary.copy(alpha = 0.15f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) it else -it }
                    ) togetherWith fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                        targetOffsetX = { if (targetState > initialState) -it else it }
                    )
                },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    0 -> KurikulumDashboardPage()
                    1 -> KurikulumKehadiranPage()
                    2 -> KurikulumGuruPenggantiPage()
                    3 -> KurikulumJadwalPage()
                    4 -> KurikulumGuruPage()
                    5 -> KurikulumProfilePage(onLogout)
                }
            }
        }
    }
}

data class KurikulumTab(val title: String, val icon: ImageVector)

// ==================== DASHBOARD PAGE ====================
@Composable
fun KurikulumDashboardPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var dashboardData by remember { mutableStateOf<KurikulumDashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    dashboardData = response.body()?.data
                } else {
                    error = response.body()?.message ?: "Gagal memuat dashboard"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error tidak diketahui"
            } finally {
                isLoading = false
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Primary, Secondary, Accent)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Selamat Datang! 👋",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Kurikulum Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    dashboardData?.let { data ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${data.hari}, ${data.tanggalFormatted}",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        } else if (error != null) {
            item {
                ErrorCard(error ?: "Error")
            }
        } else {
            dashboardData?.let { data ->
                // Summary Cards Row
                item {
                    Text(
                        text = "📊 Ringkasan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardSummaryCard(
                            title = "Total Guru",
                            value = data.summary.totalGuru.toString(),
                            icon = Icons.Default.Person,
                            color = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardSummaryCard(
                            title = "Total Jadwal",
                            value = data.summary.totalJadwal.toString(),
                            icon = Icons.Default.DateRange,
                            color = Secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardSummaryCard(
                            title = "Total Kelas",
                            value = data.summary.totalKelas.toString(),
                            icon = Icons.Default.Home,
                            color = Accent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Kehadiran Hari Ini
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Kehadiran Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                item {
                    KehadiranStatsCard(data.kehadiranHariIni)
                }
                
                // Jadwal Hari Ini
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📚 Jadwal Hari Ini (${data.jadwalHariIni.size} Jadwal)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (data.jadwalHariIni.isEmpty()) {
                    item {
                        EmptyStateCard("Tidak ada jadwal hari ini")
                    }
                } else {
                    itemsIndexed(data.jadwalHariIni.take(5)) { index, jadwal ->
                        AnimatedJadwalCard(jadwal = jadwal, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun KehadiranStatsCard(kehadiran: KehadiranSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBackground)
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                KehadiranStatItem("Hadir", kehadiran.hadir, Success)
                KehadiranStatItem("Terlambat", kehadiran.terlambat, Warning)
                KehadiranStatItem("Tidak Hadir", kehadiran.tidakHadir, Danger)
                KehadiranStatItem("Izin", kehadiran.izin, Info)
            }
            
            if (kehadiran.belumInput > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TextMuted.copy(alpha = 0.15f))
                        .border(1.dp, TextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${kehadiran.belumInput} guru belum input kehadiran",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun KehadiranStatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun AnimatedJadwalCard(jadwal: Jadwal, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 80L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Badge
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary.copy(alpha = 0.1f))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = jadwal.jamMulai,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = jadwal.jamSelesai,
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = jadwal.mataPelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.guru?.nama ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.kelas?.namaKelas ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== KEHADIRAN PAGE ====================
@Composable
fun KurikulumKehadiranPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var laporanData by remember { mutableStateOf<LaporanHarianData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getLaporanHarian()
                if (response.isSuccessful && response.body()?.success == true) {
                    laporanData = response.body()?.data
                } else {
                    error = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Success, Info)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "✅ Laporan Kehadiran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                laporanData?.let {
                    Text(
                        text = it.tanggalFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Success)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else {
            laporanData?.let { data ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary Card
                    item {
                        KehadiranStatsCard(data.summary)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📋 Detail Kehadiran Guru",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    itemsIndexed(data.detail) { index, guruStatus ->
                        AnimatedGuruStatusCard(guruStatus, index)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGuruStatusCard(guruStatus: GuruStatusHarian, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    val statusColor = when (guruStatus.status) {
        "Hadir" -> Success
        "Terlambat" -> Warning
        "Tidak Hadir" -> Danger
        "Izin" -> Info
        else -> Color.Gray
    }
    
    LaunchedEffect(index) {
        delay(index * 50L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = guruStatus.guru.nama.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guruStatus.guru.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    guruStatus.guru.mataPelajaran?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    guruStatus.jamMasuk?.let {
                        Text(
                            text = "Masuk: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = guruStatus.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ==================== JADWAL PAGE ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumJadwalPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var jadwalList by remember { mutableStateOf<List<Jadwal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedHari by remember { mutableStateOf<String?>(null) }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    
    fun loadJadwal() {
        scope.launch {
            isLoading = true
            try {
                val response = repository.getJadwal(hari = selectedHari)
                if (response.isSuccessful && response.body()?.success == true) {
                    jadwalList = response.body()?.data ?: emptyList()
                    error = null
                } else {
                    error = response.body()?.message
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(selectedHari) {
        loadJadwal()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Secondary, Accent)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "📅 Jadwal Pelajaran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Kelola jadwal semua kelas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        // Filter Hari
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedHari == null,
                    onClick = { selectedHari = null },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Secondary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(hariList) { hari ->
                FilterChip(
                    selected = selectedHari == hari,
                    onClick = { selectedHari = hari },
                    label = { Text(hari) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Secondary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Secondary)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else if (jadwalList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard("Tidak ada jadwal")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(jadwalList) { index, jadwal ->
                    AnimatedJadwalDetailCard(jadwal, index)
                }
            }
        }
    }
}

@Composable
fun AnimatedJadwalDetailCard(jadwal: Jadwal, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(index) {
        delay(index * 50L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hari Badge
                    Surface(
                        color = Secondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = jadwal.hari,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Time
                    Text(
                        text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = jadwal.mataPelajaran,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.guru?.nama ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = jadwal.kelas?.namaKelas ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== GURU PAGE ====================
@Composable
fun KurikulumGuruPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var guruList by remember { mutableStateOf<List<GuruWithStats>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = repository.getListGuru()
                if (response.isSuccessful && response.body()?.success == true) {
                    guruList = response.body()?.data ?: emptyList()
                } else {
                    error = response.body()?.message
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Info)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "👨‍🏫 Daftar Guru",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${guruList.size} guru terdaftar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorCard(error ?: "Error")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(guruList) { index, guruWithStats ->
                    AnimatedGuruCard(guruWithStats, index)
                }
            }
        }
    }
}

@Composable
fun AnimatedGuruCard(guruWithStats: GuruWithStats, index: Int) {
    var visible by remember { mutableStateOf(false) }
    
    val statusColor = when (guruWithStats.statusHariIni) {
        "Hadir" -> Success
        "Terlambat" -> Warning
        "Tidak Hadir" -> Danger
        "Izin" -> Info
        else -> Color.Gray
    }
    
    LaunchedEffect(index) {
        delay(index * 60L)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Primary, Secondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = guruWithStats.guru.nama.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = guruWithStats.guru.nama,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        guruWithStats.guru.mataPelajaran?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        guruWithStats.guru.nip?.let {
                            Text(
                                text = "NIP: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${guruWithStats.statistikBulanIni.persentaseHadir}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Success
                        )
                        Text(
                            text = "Kehadiran",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = guruWithStats.totalJadwal.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                        Text(
                            text = "Jadwal",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = guruWithStats.statusHariIni,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Hari Ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// ==================== PROFILE PAGE ====================
@Composable
fun KurikulumProfilePage(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Primary, Secondary)
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Kurikulum",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Aplikasi Monitoring Kelas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        
        // Menu Items
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileMenuCard(
                icon = Icons.Default.Info,
                title = "Tentang Aplikasi",
                subtitle = "Versi 1.0.0",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileMenuCard(
                icon = Icons.Default.Settings,
                title = "Pengaturan",
                subtitle = "Konfigurasi aplikasi",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ProfileMenuCard(
                icon = Icons.Default.ExitToApp,
                title = "Keluar",
                subtitle = "Logout dari aplikasi",
                onClick = { showLogoutDialog = true },
                iconColor = Danger,
                titleColor = Danger
            )
        }
    }
    
    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { 
                Text(
                    "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text("Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconColor: Color = Primary,
    titleColor: Color = Color.Black
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

// ==================== COMMON COMPONENTS ====================
@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, Danger.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Danger
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = Danger,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== GURU PENGGANTI PAGE ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumGuruPenggantiPage() {
    val repository = remember { KurikulumRepository() }
    val scope = rememberCoroutineScope()
    
    var guruAbsenData by remember { mutableStateOf<GuruAbsenData?>(null) }
    var guruPenggantiData by remember { mutableStateOf<GuruPenggantiListData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Dialog states
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedGuruAbsen by remember { mutableStateOf<GuruAbsenDetail?>(null) }
    var guruTersediaList by remember { mutableStateOf<List<Guru>>(emptyList()) }
    var isLoadingGuru by remember { mutableStateOf(false) }
    
    fun loadData() {
        scope.launch {
            isLoading = true
            error = null
            try {
                // Load guru absen
                val absenResponse = repository.getGuruAbsen()
                if (absenResponse.isSuccessful && absenResponse.body()?.success == true) {
                    guruAbsenData = absenResponse.body()?.data
                }
                
                // Load guru pengganti list
                val penggantiResponse = repository.getGuruPengganti()
                if (penggantiResponse.isSuccessful && penggantiResponse.body()?.success == true) {
                    guruPenggantiData = penggantiResponse.body()?.data
                }
            } catch (e: Exception) {
                error = e.message ?: "Error tidak diketahui"
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadData()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Warning, Color(0xFFE65100))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "🔄 Guru Pengganti",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Kelola penggantian guru yang tidak hadir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Warning
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Guru Absen", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Riwayat", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Warning)
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ErrorCard(error ?: "Error")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loadData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Warning)
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
        } else {
            when (selectedTabIndex) {
                0 -> GuruAbsenTab(
                    guruAbsenData = guruAbsenData,
                    onAssignClick = { detail ->
                        selectedGuruAbsen = detail
                        showAssignDialog = true
                        // Load available teachers
                        scope.launch {
                            isLoadingGuru = true
                            try {
                                val jadwalId = detail.jadwal.id ?: return@launch
                                val response = repository.getGuruTersedia(
                                    tanggal = guruAbsenData?.tanggal,
                                    jadwalId = jadwalId
                                )
                                if (response.isSuccessful && response.body()?.success == true) {
                                    guruTersediaList = response.body()?.data?.guruTersedia ?: emptyList()
                                }
                            } catch (e: Exception) {
                                // Handle error
                            } finally {
                                isLoadingGuru = false
                            }
                        }
                    }
                )
                1 -> RiwayatPenggantiTab(
                    data = guruPenggantiData,
                    onRefresh = { loadData() }
                )
            }
        }
    }
    
    // Assign Dialog
    if (showAssignDialog && selectedGuruAbsen != null) {
        AssignPenggantiDialog(
            guruAbsen = selectedGuruAbsen!!,
            guruTersedia = guruTersediaList,
            isLoading = isLoadingGuru,
            tanggal = guruAbsenData?.tanggal ?: "",
            onDismiss = { 
                showAssignDialog = false
                selectedGuruAbsen = null
            },
            onAssign = { guruPenggantiId, alasan, keterangan ->
                scope.launch {
                    try {
                        val jadwalId = selectedGuruAbsen!!.jadwal.id ?: return@launch
                        val guruAsliId = selectedGuruAbsen!!.guru.id ?: return@launch
                        val request = GuruPenggantiRequest(
                            tanggal = guruAbsenData?.tanggal ?: "",
                            jadwalId = jadwalId,
                            guruAsliId = guruAsliId,
                            guruPenggantiId = guruPenggantiId,
                            alasan = alasan,
                            keterangan = keterangan
                        )
                        val response = repository.createGuruPengganti(request)
                        if (response.isSuccessful && response.body()?.success == true) {
                            showAssignDialog = false
                            selectedGuruAbsen = null
                            loadData() // Refresh data
                        }
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        )
    }
}

@Composable
fun GuruAbsenTab(
    guruAbsenData: GuruAbsenData?,
    onAssignClick: (GuruAbsenDetail) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = guruAbsenData?.hari ?: "-",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = guruAbsenData?.tanggalFormatted ?: "-",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${guruAbsenData?.totalAbsen ?: 0}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Danger
                                )
                                Text("Guru Absen", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${guruAbsenData?.totalJadwalTerdampak ?: 0}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Warning
                                )
                                Text("Jadwal", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
        
        if (guruAbsenData?.detail.isNullOrEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Semua Guru Hadir!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Success
                        )
                        Text(
                            text = "Tidak ada guru yang perlu digantikan hari ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(guruAbsenData?.detail ?: emptyList()) { detail ->
                GuruAbsenCard(
                    detail = detail,
                    onAssignClick = { onAssignClick(detail) }
                )
            }
        }
    }
}

@Composable
fun GuruAbsenCard(
    detail: GuruAbsenDetail,
    onAssignClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Guru Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Danger.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = detail.guru.nama.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Danger
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = detail.guru.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (detail.status == "Izin") Info.copy(alpha = 0.1f)
                                    else Danger.copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = detail.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (detail.status == "Izin") Info else Danger,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        detail.keterangan?.let {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.Gray.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Jadwal Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = detail.jadwal.mataPelajaran,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = detail.jadwal.kelas?.namaKelas ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${detail.jadwal.jamMulai} - ${detail.jadwal.jamSelesai}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Button or Status
            if (detail.sudahAdaPengganti && detail.pengganti != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Success.copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Digantikan oleh:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Success
                        )
                        Text(
                            text = detail.pengganti?.guruPengganti?.nama ?: "-",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Success
                        )
                    }
                }
            } else {
                Button(
                    onClick = onAssignClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Warning),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assign Guru Pengganti")
                }
            }
        }
    }
}

@Composable
fun RiwayatPenggantiTab(
    data: GuruPenggantiListData?,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistik Penggantian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatColumn("Total", data?.stats?.total ?: 0, Primary)
                        StatColumn("Pending", data?.stats?.pending ?: 0, Warning)
                        StatColumn("Disetujui", data?.stats?.disetujui ?: 0, Success)
                        StatColumn("Selesai", data?.stats?.selesai ?: 0, Color.Gray)
                    }
                }
            }
        }
        
        if (data?.list.isNullOrEmpty()) {
            item {
                EmptyStateCard("Belum ada riwayat penggantian guru")
            }
        } else {
            items(data?.list ?: emptyList()) { item ->
                RiwayatPenggantiCard(item)
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun RiwayatPenggantiCard(item: GuruPenggantiItem) {
    val statusColor = when (item.status) {
        "Pending" -> Warning
        "Disetujui" -> Success
        "Ditolak" -> Danger
        "Selesai" -> Color.Gray
        else -> Color.Gray
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.tanggal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.jadwal?.mataPelajaran ?: "-",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Guru Asli", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = item.guruAsli?.nama ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pengganti", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = item.guruPengganti?.nama ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Success
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Alasan: ${item.alasan}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignPenggantiDialog(
    guruAbsen: GuruAbsenDetail,
    guruTersedia: List<Guru>,
    isLoading: Boolean,
    tanggal: String,
    onDismiss: () -> Unit,
    onAssign: (guruPenggantiId: Int, alasan: String, keterangan: String?) -> Unit
) {
    var selectedGuruId by remember { mutableStateOf<Int?>(null) }
    var selectedAlasan by remember { mutableStateOf(guruAbsen.status) }
    var keterangan by remember { mutableStateOf(guruAbsen.keterangan ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }
    
    val alasanOptions = listOf("Sakit", "Izin", "Cuti", "Dinas Luar", "Lainnya")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Assign Guru Pengganti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${guruAbsen.jadwal.mataPelajaran} - ${guruAbsen.jadwal.kelas?.namaKelas ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Guru Asli Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Danger)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Guru yang digantikan:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = guruAbsen.guru.nama,
                                fontWeight = FontWeight.Bold,
                                color = Danger
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Pilih Guru Pengganti
                Text(
                    text = "Pilih Guru Pengganti:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                } else if (guruTersedia.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Warning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tidak ada guru yang tersedia pada jam ini",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Warning
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(150.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(guruTersedia) { guru ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedGuruId = guru.id },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedGuruId == guru.id) 
                                        Success.copy(alpha = 0.1f) else Color.White
                                ),
                                border = if (selectedGuruId == guru.id) 
                                    BorderStroke(2.dp, Success) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedGuruId == guru.id,
                                        onClick = { selectedGuruId = guru.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = Success)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = guru.nama,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = guru.mataPelajaran ?: "-",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Alasan
                Text(
                    text = "Alasan:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alasanOptions.forEach { alasan ->
                        FilterChip(
                            selected = selectedAlasan == alasan,
                            onClick = { selectedAlasan = alasan },
                            label = { Text(alasan) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Keterangan
                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedGuruId?.let { guruId ->
                        isSubmitting = true
                        onAssign(guruId, selectedAlasan, keterangan.ifEmpty { null })
                    }
                },
                enabled = selectedGuruId != null && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Success)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Assign")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
