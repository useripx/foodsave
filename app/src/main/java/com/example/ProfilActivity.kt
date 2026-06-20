package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

class ProfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val sharedPref = getSharedPreferences("foodsave_prefs", Context.MODE_PRIVATE)
                
                var userName by remember { mutableStateOf(sharedPref.getString("user_name", "Eco Hero") ?: "Eco Hero") }
                var userEmail by remember { mutableStateOf(sharedPref.getString("user_email", "hero@foodsave.com") ?: "hero@foodsave.com") }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ProfilTopAppBar(onBackClick = { finish() })
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    ProfilScreen(
                        modifier = Modifier.padding(innerPadding),
                        userName = userName,
                        userEmail = userEmail,
                        onUpdateProfile = { name, email ->
                            with(sharedPref.edit()) {
                                putString("user_name", name)
                                putString("user_email", email)
                                apply()
                            }
                            userName = name
                            userEmail = email
                            Toast.makeText(this@ProfilActivity, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        },
                        onLogoutClick = {
                            // Clear logged in session
                            with(sharedPref.edit()) {
                                putBoolean("is_logged_in", false)
                                apply()
                            }
                            // Back to LoginActivity
                            val intent = Intent(this@ProfilActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilTopAppBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Profil Pengguna",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenDark,
                    fontSize = 18.sp
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back icon",
                    tint = GreenDark
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.shadow(2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String,
    onUpdateProfile: (String, String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Identity card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circular profile avatar with dynamic gradient as shown in screen 5!
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GreenPrimary, GreenDark)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Monogram or circular face
                        Text(
                            text = userName.take(2).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        
                        // Small edit overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(OrangeAccent, shape = CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit photo indicator",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextGray
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons Row: Edit Profile and Keluar/Logout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Edit Profil Button
                        OutlinedButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_edit_profile"),
                            shape = RoundedCornerShape(22.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark)
                        ) {
                            Text(
                                text = "Edit Profil",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        // Logout / Keluar button
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_logout"),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedLight,
                                contentColor = RedError
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Keluar",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Activites
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AKTIVITAS",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextGray,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                // Pantry Saya Row
                ActivityItemRow(
                    icon = "🧺",
                    title = "Pantry Saya",
                    onClick = { Toast.makeText(context, "Sama dengan fitur Daftar Makanan", Toast.LENGTH_SHORT).show() }
                )
                // Riwayat Donasi Row
                ActivityItemRow(
                    icon = "🤝",
                    title = "Riwayat Donasi",
                    onClick = { Toast.makeText(context, "Riwayat Donasi segera hadir!", Toast.LENGTH_SHORT).show() }
                )
                // Pengaturan Akun Row
                ActivityItemRow(
                    icon = "⚙️",
                    title = "Pengaturan Akun",
                    onClick = { Toast.makeText(context, "Fitur pengaturan akun draf!", Toast.LENGTH_SHORT).show() }
                )
            }
        }

        // Section Achievements / Pencapaian
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PENCAPAIAN",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextGray,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Badge 1: First Save
                    BadgeCardItem(
                        modifier = Modifier.weight(1f),
                        emoji = "🌱",
                        bgGradient = listOf(GreenLight, GreenLight.copy(alpha = 0.3f)),
                        title = "First Save",
                        desc = "Berhasil mencatat makanan pertama"
                    )

                    // Badge 2: Community Hero
                    BadgeCardItem(
                        modifier = Modifier.weight(1f),
                        emoji = "👥",
                        bgGradient = listOf(Color(0xFFE3F2FD), Color(0xFFE3F2FD).copy(alpha = 0.3f)),
                        title = "Eco Hero",
                        desc = "Aktif menyelamatkan ekosistem"
                    )

                    // Badge 3: 100kg Saver
                    BadgeCardItem(
                        modifier = Modifier.weight(1f),
                        emoji = "⭐",
                        bgGradient = listOf(YellowLight, YellowLight.copy(alpha = 0.3f)),
                        title = "100kg Saver",
                        desc = "Menghemat bahan pangan secara massal"
                    )
                }
            }
        }
    }

    // Edit profile dialog modal
    if (showEditDialog) {
        var tempName by remember { mutableStateOf(userName) }
        var tempEmail by remember { mutableStateOf(userEmail) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Profil Pengguna",
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nama Pengguna") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank() && tempEmail.isNotBlank()) {
                            onUpdateProfile(tempName, tempEmail)
                            showEditDialog = false
                        } else {
                            Toast.makeText(context, "Kolom tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ActivityItemRow(
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(OffWhiteBg, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = TextDark,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow right icon",
                tint = TextGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun BadgeCardItem(
    modifier: Modifier = Modifier,
    emoji: String,
    bgGradient: List<Color>,
    title: String,
    desc: String
) {
    Card(
        modifier = modifier
            .shadow(1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.radialGradient(colors = bgGradient),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextGray,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}
