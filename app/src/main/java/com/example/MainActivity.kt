package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.theme.*
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val app = application as FoodSaveApplication
                val repository = app.repository
                val foodItems by repository.allFoodItems.collectAsState(initial = emptyList())
                
                // Get user name from preferences
                val sharedPref = getSharedPreferences("foodsave_prefs", Context.MODE_PRIVATE)
                val userName = sharedPref.getString("user_name", "Eco Hero") ?: "Eco Hero"

                var showReminderDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        DashboardAppBar(
                            userName = userName,
                            onProfileClick = {
                                val intent = Intent(this@MainActivity, ProfilActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    },
                    bottomBar = {
                        DashboardBottomBar(
                            onTabClick = { tab ->
                                if (tab == "Settings" || tab == "Profil") {
                                    val intent = Intent(this@MainActivity, ProfilActivity::class.java)
                                    startActivity(intent)
                                } else if (tab == "Pengingat") {
                                    showReminderDialog = true
                                } else if (tab == "Stok") {
                                    val intent = Intent(this@MainActivity, DaftarMakananActivity::class.java)
                                    startActivity(intent)
                                } else if (tab == "Tambah") {
                                    val intent = Intent(this@MainActivity, TambahMakananActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@MainActivity, "$tab segera hadir!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    DashboardScreen(
                        modifier = Modifier.padding(innerPadding),
                        foodItems = foodItems,
                        userName = userName,
                        onTambahClick = {
                            val intent = Intent(this@MainActivity, TambahMakananActivity::class.java)
                            startActivity(intent)
                        },
                        onDaftarClick = {
                            val intent = Intent(this@MainActivity, DaftarMakananActivity::class.java)
                            startActivity(intent)
                        },
                        onProfilClick = {
                            val intent = Intent(this@MainActivity, ProfilActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }

                if (showReminderDialog) {
                    val now = System.currentTimeMillis()
                    val fiveDaysFromNow = now + (5L * 24 * 60 * 60 * 1000)
                    val expiringItems = foodItems.filter { it.expiryDate <= fiveDaysFromNow }

                    AlertDialog(
                        onDismissRequest = { showReminderDialog = false },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🔔", fontSize = 24.sp)
                                Text(
                                    text = "Pengingat Kadaluarsa",
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    fontSize = 18.sp
                                )
                            }
                        },
                        text = {
                            if (expiringItems.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🎉", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Pantry Aman & Segar!",
                                        fontWeight = FontWeight.Bold,
                                        color = GreenDark,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tidak ada bahan makanan yang mendekati masa kadaluarsa dalam 5 hari.",
                                        color = TextGray,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Bahan pangan berikut mendekati kadaluarsa dalam ≤ 5 hari atau sudah kadaluarsa:",
                                        color = TextGray,
                                        fontSize = 12.sp
                                    )
                                    
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 280.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(expiringItems) { item ->
                                            val diffMillis = item.expiryDate - now
                                            val daysLeft = if (diffMillis <= 0) 0 else (diffMillis / (24 * 60 * 60 * 1000)).toInt() + 1
                                            
                                            val (statusColor, statusBg) = when {
                                                daysLeft <= 0 -> Pair(RedError, RedLight)
                                                daysLeft <= 2 -> Pair(RedError, RedLight)
                                                else -> Pair(YellowWarning, YellowLight)
                                            }

                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = OffWhiteBg),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = item.name,
                                                            fontWeight = FontWeight.Bold,
                                                            color = TextDark,
                                                            fontSize = 13.sp
                                                        )
                                                        Text(
                                                            text = "Kategori: ${item.category}",
                                                            color = TextGray,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .background(statusBg, shape = RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (daysLeft == 0) "KEDALUARSA" else "$daysLeft HARI LAGI",
                                                            color = if (statusColor == YellowWarning) OrangeDeep else statusColor,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { showReminderDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Tutup", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAppBar(
    userName: String,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "FoodSave Dashboard",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenDark,
                    fontSize = 18.sp
                )
            )
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(36.dp)
                    .background(GreenLight, shape = CircleShape)
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = GreenDark,
                    fontSize = 12.sp
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            scrolledContainerColor = Color.White
        ),
        modifier = Modifier.shadow(2.dp)
    )
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    foodItems: List<FoodItem>,
    userName: String,
    onTambahClick: () -> Unit,
    onDaftarClick: () -> Unit,
    onProfilClick: () -> Unit
) {
    // Compute stats
    val totalItems = foodItems.size
    
    val now = System.currentTimeMillis()
    val twoDaysFromNow = now + (2 * 24 * 60 * 60 * 1000)
    
    val expiringSoonCount = foodItems.count { it.expiryDate <= twoDaysFromNow && it.expiryDate > now }
    val savedAmount = totalItems * 35000 // estimate Rp 35k saved per entered item

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Halo, $userName!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = "Kelola bahan pangan Anda, kurangi sampah organik.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                )
            }
        }

        // Summary Card row
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Stat 1: Total
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Text(
                                text = "$totalItems",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDark,
                                    fontSize = 32.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .offset(x = 12.dp, y = (-2).dp)
                                    .background(OrangeAccent, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = Color.White,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Item Terdaftar",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGray, fontSize = 11.sp)
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp),
                        color = GrayBorder
                    )

                    // Stat 2: Expiring
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚠️",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$expiringSoonCount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeDeep
                                )
                            )
                        }
                        Text(
                            text = "Segera Kadaluarsa",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGray, fontSize = 11.sp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp),
                        color = GrayBorder
                    )

                    // Stat 3: Saved value conversion
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🐖",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (savedAmount > 1000) "Rp ${savedAmount / 1000}k" else "Rp $savedAmount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            )
                        }
                        Text(
                            text = "Nilai Terselamatkan",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGray, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Menu Utama",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 1. Tambah Makanan Card Button (ID: btnTambah)
        item {
            MenuButtonCard(
                testTag = "btnTambah",
                title = "Tambah Makanan",
                description = "Catat makanan baru Anda untuk memantau masa kadaluarsa dan mengurangi limbah.",
                icon = "➕",
                iconBg = GreenLight,
                onClick = onTambahClick
            )
        }

        // 2. Daftar Makanan Card Button (ID: btnDaftar)
        item {
            MenuButtonCard(
                testTag = "btnDaftar",
                title = "Daftar Makanan",
                description = "Lihat daftar stok bahan pangan Anda lengkap dengan status kesegaran berwarna.",
                icon = "📦",
                iconBg = OrangeLight,
                onClick = onDaftarClick
            )
        }

        // 3. Profil Card Button (ID: btnProfil)
        item {
            MenuButtonCard(
                testTag = "btnProfil",
                title = "Profil Saya",
                description = "Kelola data akun Anda, dan pantau total berat makanan yang diselamatkan.",
                icon = "👤",
                iconBg = GreenLight,
                onClick = onProfilClick
            )
        }

        // Pro Tip Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWarm)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "💡",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Pro Tip",
                            fontWeight = FontWeight.Bold,
                            color = OrangeDeep,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Memindahkan buah apel lama ke bagian depan kulkas dapat mengurangi pemborosan makanan hingga 20%!",
                            color = TextDark,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButtonCard(
    testTag: String,
    title: String,
    description: String,
    icon: String,
    iconBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow right icon",
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DashboardBottomBar(
    onTabClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Inventory") },
            label = { Text("Inventory", fontSize = 11.sp) },
            selected = true,
            onClick = { onTabClick("Inventory") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenDark,
                selectedTextColor = GreenDark,
                indicatorColor = GreenLight
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Stok") },
            label = { Text("Stok", fontSize = 11.sp) },
            selected = false,
            onClick = { onTabClick("Stok") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Tambah") },
            label = { Text("Tambah", fontSize = 11.sp) },
            selected = false,
            onClick = { onTabClick("Tambah") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Pengingat") },
            label = { Text("Pengingat", fontSize = 11.sp) },
            selected = false,
            onClick = { onTabClick("Pengingat") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil", fontSize = 11.sp) },
            selected = false,
            onClick = { onTabClick("Profil") }
        )
    }
}
