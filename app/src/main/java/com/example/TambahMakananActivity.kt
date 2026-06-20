package com.example

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.FoodItem
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TambahMakananActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val app = application as FoodSaveApplication
                val repository = app.repository

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TambahTopAppBar(onBackClick = { finish() })
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    TambahScreen(
                        modifier = Modifier.padding(innerPadding),
                        onSaveClick = { foodItem ->
                            // Perform database insert inside coroutines scope
                            lifecycleScope.launch {
                                repository.insert(foodItem)
                                Toast.makeText(this@TambahMakananActivity, "Makanan '${foodItem.name}' berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahTopAppBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Tambah Makanan",
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

@Composable
fun TambahScreen(
    modifier: Modifier = Modifier,
    onSaveClick: (FoodItem) -> Unit
) {
    val context = LocalContext.current
    
    // Inputs state
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Susu & Olahan") } // Default to Dairy category
    var quantityString by remember { mutableStateOf("1") }
    var quantityUnit by remember { mutableStateOf("pcs") }

    // Date picker state
    val calendar = remember { Calendar.getInstance() }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) } // Indonesian formatting
    var expiryDateText by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDateMillis = calendar.timeInMillis
            expiryDateText = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Prepopulate some default date text (e.g., today + 7 days) if empty
    LaunchedEffect(Unit) {
        if (expiryDateText.isEmpty()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            selectedDateMillis = calendar.timeInMillis
            expiryDateText = dateFormatter.format(calendar.time)
        }
    }

    val categories = listOf("Protein", "Sayuran", "Buah", "Susu & Olahan")
    val units = listOf("pcs", "Liter", "Units", "Bundles")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main input container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header decoration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GreenLight, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🥬", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Informasi Bahan Pangan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                }

                Divider(color = GrayBorder.copy(alpha = 0.5f))

                // 1. Nama Makanan Input
                Column {
                    Text(
                        text = "Nama Makanan",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Contoh: Susu Sapi Segar", color = TextGray.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedContainerColor = OffWhiteBg,
                            unfocusedContainerColor = OffWhiteBg
                        ),
                        singleLine = true
                    )
                }

                // 2. Kategori Selection (Visual Chips as requested!)
                Column {
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Grid of chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            val chipBg = if (isSelected) GreenPrimary else Color.White
                            val chipText = if (isSelected) Color.White else TextDark
                            val chipBorder = if (isSelected) GreenPrimary else GrayBorder
                            val emoji = when(cat) {
                                "Protein" -> "🍗"
                                "Sayuran" -> "🥦"
                                "Buah" -> "🍎"
                                else -> "🥛"
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(chipBg)
                                    .border(1.dp, chipBorder, RoundedCornerShape(10.dp))
                                    .clickable { category = cat }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = chipText,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Jumlah & Unit row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Qty Field
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = "Jumlah",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = quantityString,
                            onValueChange = { quantityString = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quantity_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = GrayBorder,
                                focusedContainerColor = OffWhiteBg,
                                unfocusedContainerColor = OffWhiteBg
                            ),
                            singleLine = true
                        )
                    }

                    // Unit selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Satuan",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        var expanded by remember { mutableStateOf(false) }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OffWhiteBg)
                                .border(1.dp, GrayBorder, RoundedCornerShape(12.dp))
                                .clickable { expanded = !expanded }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = quantityUnit, color = TextDark, fontWeight = FontWeight.Medium)
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                units.forEach { itemUnit ->
                                    DropdownMenuItem(
                                        text = { Text(itemUnit) },
                                        onClick = {
                                            quantityUnit = itemUnit
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Tanggal Kadaluarsa Input
                Column {
                    Text(
                        text = "Tanggal Kadaluarsa",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = expiryDateText,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Trigger Date Picker icon",
                                    tint = GreenPrimary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                            .testTag("expiry_date_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedContainerColor = OffWhiteBg,
                            unfocusedContainerColor = OffWhiteBg
                        ),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tips Hemat Box & Simpan button
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tips Hemat
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWarm)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tips Hemat",
                            fontWeight = FontWeight.Bold,
                            color = OrangeDeep,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Simpan produk susu di bagian belakang kulkas (bukan di pintu) agar suhunya tetap dingin lebih konsisten!",
                            color = TextDark,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Simpan Button (ID: btnSimpan)
            Button(
                onClick = {
                    val finalQty = quantityString.toIntOrNull() ?: 1
                    if (name.isBlank()) {
                        Toast.makeText(context, "Silakan isi nama makanan terlebih dahulu", Toast.LENGTH_SHORT).show()
                    } else {
                        val foodItem = FoodItem(
                            name = name,
                            category = category,
                            quantity = finalQty,
                            quantityUnit = quantityUnit,
                            expiryDate = selectedDateMillis
                        )
                        onSaveClick(foodItem)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btnSimpan"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(27.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Simpan Makanan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
