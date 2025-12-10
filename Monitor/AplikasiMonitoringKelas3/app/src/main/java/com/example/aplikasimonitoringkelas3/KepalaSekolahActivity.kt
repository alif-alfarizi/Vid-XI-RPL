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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas3.data.repository.KepalaSekolahRepository
import com.example.aplikasimonitoringkelas3.ui.theme.AplikasiMonitoringKelas3Theme
import kotlinx.coroutines.launch

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userName = intent.getStringExtra("USER_NAME") ?: "Kepala Sekolah"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        
        setContent {
            AplikasiMonitoringKelas3Theme {
                KepalaSekolahScreen(
                    userName = userName,
                    userEmail = userEmail,
                    onLogout = {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Tren", "Ranking", "Laporan", "Profil")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Star,
        Icons.Default.Favorite,
        Icons.Default.DateRange,
        Icons.Default.Person
    )
    
    // Executive color scheme - Navy & Gold
    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))
    )
    val accentColor = Color(0xFFFFB300) // Gold
    
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kepala Sekolah",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Crown Icon for Principal
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(accentColor, Color(0xFFFF8F00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Kepala Sekolah",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.shadow(16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = title,
                                modifier = Modifier.size(if (selectedTab == index) 28.dp else 24.dp)
                            )
                        },
                        label = {
                            Text(
                                title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1A237E),
                            selectedTextColor = Color(0xFF1A237E),
                            indicatorColor = accentColor.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
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
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedTab) {
                0 -> DashboardExecutiveTab()
                1 -> TrenKehadiranTab()
                2 -> RankingGuruTab()
                3 -> LaporanTab()
                4 -> ProfilKepsekTab(userName, userEmail, onLogout)
            }
        }
    }
}

// ==================== DASHBOARD EXECUTIVE TAB ====================

@Composable
fun DashboardExecutiveTab() {
    val repository = remember { KepalaSekolahRepository() }
    var dashboardData by remember { mutableStateOf<KepsekDashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getDashboard()
                .onSuccess { dashboardData = it; isLoading = false }
                .onFailure { errorMessage = it.message; isLoading = false }
        }
    }
    
    val accentColor = Color(0xFFFFB300)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card dengan Status Hari Ini
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "📊 Dashboard Executive",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = dashboardData?.hari ?: "Hari Ini",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            
                            // Animated pulse indicator
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .size((16 * scale).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Main KPI
                        if (dashboardData != null) {
                            val persentase = dashboardData!!.kehadiranHariIni.persentaseHadir
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Big Percentage
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${persentase.toInt()}%",
                                        color = accentColor,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Kehadiran Hari Ini",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                }
                                
                                // Vertical Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(60.dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
                                
                                // Quick Stats
                                Column(horizontalAlignment = Alignment.Start) {
                                    QuickStatRow("✅ Hadir", dashboardData!!.kehadiranHariIni.hadir)
                                    QuickStatRow("❌ Tidak Hadir", dashboardData!!.kehadiranHariIni.tidakHadir)
                                    QuickStatRow("📝 Izin", dashboardData!!.kehadiranHariIni.izin)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Summary Cards Row
        item {
            if (dashboardData != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryKPICard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Face,
                        title = "Total Guru",
                        value = "${dashboardData!!.summary.totalGuru}",
                        gradient = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                    )
                    SummaryKPICard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Menu,
                        title = "Total Kelas",
                        value = "${dashboardData!!.summary.totalKelas}",
                        gradient = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                    )
                }
            }
        }
        
        item {
            if (dashboardData != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryKPICard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Info,
                        title = "Jadwal Hari Ini",
                        value = "${dashboardData!!.summary.jadwalHariIni}",
                        gradient = listOf(Color(0xFFf093fb), Color(0xFFf5576c))
                    )
                    SummaryKPICard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.DateRange,
                        title = "Total Jadwal",
                        value = "${dashboardData!!.summary.totalJadwal}",
                        gradient = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
                    )
                }
            }
        }
        
        // Performa Bulanan Card
        item {
            if (dashboardData != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📈 Performa Bulanan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                            
                            // Trend Badge
                            val trend = dashboardData!!.performaBulanan.trend
                            val selisih = dashboardData!!.performaBulanan.selisih
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (trend == "naik") Color(0xFF4CAF50).copy(alpha = 0.1f)
                                       else Color(0xFFF44336).copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (trend == "naik") Icons.Default.KeyboardArrowUp 
                                                      else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = if (trend == "naik") Color(0xFF4CAF50) else Color(0xFFF44336),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${if (selisih >= 0) "+" else ""}${selisih}%",
                                        color = if (trend == "naik") Color(0xFF4CAF50) else Color(0xFFF44336),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Monthly Comparison
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MonthCompareCard(
                                title = "Bulan Lalu",
                                month = dashboardData!!.performaBulanan.bulanLalu.bulan,
                                percentage = dashboardData!!.performaBulanan.bulanLalu.persentase,
                                isHighlighted = false
                            )
                            
                            // Arrow
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            
                            MonthCompareCard(
                                title = "Bulan Ini",
                                month = dashboardData!!.performaBulanan.bulanIni.bulan,
                                percentage = dashboardData!!.performaBulanan.bulanIni.persentase,
                                isHighlighted = true
                            )
                        }
                    }
                }
            }
        }
        
        // Loading State
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            }
        }
        
        // Error State
        if (errorMessage != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatRow(label: String, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SummaryKPICard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    gradient: List<Color>
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
                
                Column {
                    Text(
                        text = value,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MonthCompareCard(
    title: String,
    month: String,
    percentage: Double,
    isHighlighted: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(if (isHighlighted) 8.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    if (isHighlighted) 
                        Brush.radialGradient(listOf(Color(0xFF1A237E), Color(0xFF3949AB)))
                    else 
                        Brush.radialGradient(listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${percentage.toInt()}%",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = month.take(3),
            fontSize = 11.sp,
            color = if (isHighlighted) Color(0xFF1A237E) else Color.Gray,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ==================== TREN KEHADIRAN TAB ====================

@Composable
fun TrenKehadiranTab() {
    val repository = remember { KepalaSekolahRepository() }
    var trenMingguan by remember { mutableStateOf<TrenMingguanData?>(null) }
    var trenBulanan by remember { mutableStateOf<TrenBulananData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedView by remember { mutableStateOf("mingguan") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getTrenMingguan().onSuccess { trenMingguan = it }
            repository.getTrenBulanan().onSuccess { trenBulanan = it }
            isLoading = false
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // View Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("mingguan" to "📅 Mingguan", "bulanan" to "📆 Bulanan").forEach { (key, label) ->
                        FilterChip(
                            onClick = { selectedView = key },
                            label = { Text(label, fontWeight = FontWeight.Medium) },
                            selected = selectedView == key,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1A237E),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            }
        } else {
            if (selectedView == "mingguan" && trenMingguan != null) {
                // Weekly Chart Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📊 Tren 7 Hari Terakhir",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A237E)
                                )
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFB300).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Rata-rata: ${trenMingguan!!.rataRata}%",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = Color(0xFFFF8F00),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Bar Chart Visual
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                trenMingguan!!.tren.forEach { item ->
                                    TrendBarItem(
                                        label = item.hariSingkat,
                                        percentage = item.persentase.toFloat(),
                                        hadir = item.hadir,
                                        total = item.total
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Daily Details
                item {
                    Text(
                        text = "📋 Detail Harian",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(trenMingguan!!.tren.reversed()) { item ->
                    TrendDayCard(item)
                }
                
            } else if (selectedView == "bulanan" && trenBulanan != null) {
                // Monthly Overview
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "📅 Tren Bulanan ${trenBulanan!!.tahun}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Monthly Grid
                            val chunkedMonths = trenBulanan!!.tren.chunked(4)
                            chunkedMonths.forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { month ->
                                        MonthTrendCard(month)
                                    }
                                    // Fill empty slots if needed
                                    repeat(4 - row.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendBarItem(
    label: String,
    percentage: Float,
    hadir: Int,
    total: Int
) {
    val animatedHeight by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "barHeight"
    )
    
    val barColor = when {
        percentage >= 80 -> Color(0xFF4CAF50)
        percentage >= 60 -> Color(0xFFFFB300)
        else -> Color(0xFFF44336)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(40.dp)
    ) {
        Text(
            text = "${percentage.toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = barColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .width(30.dp)
                .height((animatedHeight * 1.2f).dp.coerceAtLeast(8.dp))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(barColor, barColor.copy(alpha = 0.6f))
                    )
                )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun TrendDayCard(item: TrenHarian) {
    val barColor = when {
        item.persentase >= 80 -> Color(0xFF4CAF50)
        item.persentase >= 60 -> Color(0xFFFFB300)
        else -> Color(0xFFF44336)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.hari,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = item.tanggal,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "✅ ${item.hadir} | ❌ ${item.tidakHadir} | 📝 ${item.izin}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = barColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${item.persentase.toInt()}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = barColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MonthTrendCard(month: TrenBulanan) {
    val color = when {
        month.persentase >= 80 -> Color(0xFF4CAF50)
        month.persentase >= 60 -> Color(0xFFFFB300)
        month.persentase > 0 -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (month.total > 0)
                        Brush.radialGradient(listOf(color, color.copy(alpha = 0.7f)))
                    else
                        Brush.radialGradient(listOf(Color.LightGray, Color.Gray))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (month.total > 0) "${month.persentase.toInt()}%" else "-",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = month.namaBulanSingkat,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

// ==================== RANKING GURU TAB ====================

@Composable
fun RankingGuruTab() {
    val repository = remember { KepalaSekolahRepository() }
    var rankingData by remember { mutableStateOf<RankingGuruData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedView by remember { mutableStateOf("all") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getRankingGuru()
                .onSuccess { rankingData = it; isLoading = false }
                .onFailure { isLoading = false }
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🏆 Ranking Kehadiran",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (rankingData != null) {
                                Text(
                                    text = "${rankingData!!.periode.bulan} ${rankingData!!.periode.tahun}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
        
        // View Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "all" to "📊 Semua",
                    "top" to "⭐ Terbaik",
                    "attention" to "⚠️ Perlu Perhatian"
                ).forEach { (key, label) ->
                    FilterChip(
                        onClick = { selectedView = key },
                        label = { Text(label, fontSize = 12.sp) },
                        selected = selectedView == key,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1A237E),
                            selectedLabelColor = Color.White
                        )
                    )
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
                    CircularProgressIndicator(color = Color(0xFFFFB300))
                }
            }
        } else if (rankingData != null) {
            val displayList = when (selectedView) {
                "top" -> rankingData!!.topPerformers
                "attention" -> rankingData!!.needAttention
                else -> rankingData!!.ranking
            }
            
            items(displayList) { guru ->
                RankingGuruCard(guru, selectedView == "attention")
            }
        }
    }
}

@Composable
fun RankingGuruCard(guru: GuruRanking, isAttention: Boolean) {
    val rankColor = when (guru.ranking) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF1A237E)
    }
    
    val percentageColor = when {
        guru.persentase >= 80 -> Color(0xFF4CAF50)
        guru.persentase >= 60 -> Color(0xFFFFB300)
        else -> Color(0xFFF44336)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAttention) Color(0xFFFFF3E0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (guru.ranking <= 3)
                            Brush.radialGradient(listOf(rankColor, rankColor.copy(alpha = 0.7f)))
                        else
                            Brush.radialGradient(listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (guru.ranking <= 3) {
                    Text(
                        text = when (guru.ranking) {
                            1 -> "🥇"
                            2 -> "🥈"
                            else -> "🥉"
                        },
                        fontSize = 20.sp
                    )
                } else {
                    Text(
                        text = "#${guru.ranking}",
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guru.nama,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A237E)
                )
                guru.nip?.let {
                    Text(
                        text = "NIP: $it",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                guru.mataPelajaran?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Stats Row
                Row {
                    Text(
                        text = "✅${guru.hadir} ❌${guru.tidakHadir} 📝${guru.izin}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Percentage Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(percentageColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${guru.persentase.toInt()}%",
                    color = percentageColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ==================== LAPORAN TAB ====================

@Composable
fun LaporanTab() {
    val repository = remember { KepalaSekolahRepository() }
    var laporanData by remember { mutableStateOf<LaporanKepsekData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getLaporan()
                .onSuccess { laporanData = it; isLoading = false }
                .onFailure { isLoading = false }
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "📑 Laporan Bulanan",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (laporanData != null) {
                            Text(
                                text = laporanData!!.periode.bulan,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 16.sp
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
                    CircularProgressIndicator(color = Color(0xFF667eea))
                }
            }
        } else if (laporanData != null) {
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LaporanStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Record",
                        value = "${laporanData!!.ringkasan.totalKehadiran}",
                        icon = "📊",
                        color = Color(0xFF667eea)
                    )
                    LaporanStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Hadir",
                        value = "${laporanData!!.ringkasan.hadir}",
                        icon = "✅",
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LaporanStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Tidak Hadir",
                        value = "${laporanData!!.ringkasan.tidakHadir}",
                        icon = "❌",
                        color = Color(0xFFF44336)
                    )
                    LaporanStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Izin",
                        value = "${laporanData!!.ringkasan.izin}",
                        icon = "📝",
                        color = Color(0xFFFFB300)
                    )
                }
            }
            
            // Percentage Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Persentase Kehadiran",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val percentage = laporanData!!.ringkasan.persentaseHadir
                            val color = when {
                                percentage >= 80 -> Color(0xFF4CAF50)
                                percentage >= 60 -> Color(0xFFFFB300)
                                else -> Color(0xFFF44336)
                            }
                            
                            Text(
                                text = "${percentage.toInt()}%",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                            
                            // Progress Bar
                            LinearProgressIndicator(
                                progress = { (percentage / 100).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
            
            // Guru Terbaik
            item {
                Text(
                    text = "⭐ Guru Terbaik Bulan Ini",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
            
            items(laporanData!!.guruTerbaik) { guru ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍🏫", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = guru.nama,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4CAF50)
                        ) {
                            Text(
                                text = "${guru.totalHadir}x Hadir",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            // Guru Perlu Perhatian
            item {
                Text(
                    text = "⚠️ Perlu Perhatian",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(laporanData!!.guruPerhatian) { guru ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍🏫", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = guru.nama,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFC62828)
                            )
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF44336)
                        ) {
                            Text(
                                text = "${guru.totalTidakHadir}x Absen",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaporanStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 28.sp)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

// ==================== PROFIL TAB ====================

@Composable
fun ProfilKepsekTab(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFB300).copy(alpha = 0.2f),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "👑 Kepala Sekolah",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color(0xFFFF8F00),
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ProfileInfoRowKepsek(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = userEmail
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                ProfileInfoRowKepsek(
                    icon = Icons.Default.Check,
                    label = "Role",
                    value = "Kepala Sekolah"
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                ProfileInfoRowKepsek(
                    icon = Icons.Default.AccountBox,
                    label = "Institusi",
                    value = "SMKN 2"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logout Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFF44336)
                )
            },
            title = {
                Text(
                    text = "Konfirmasi Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Apakah Anda yakin ingin keluar dari aplikasi?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Logout")
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
fun ProfileInfoRowKepsek(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A237E).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1A237E),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A237E)
            )
        }
    }
}
