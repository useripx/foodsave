package com.example

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.FoodItem
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class DaftarMakananActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val app = application as FoodSaveApplication
                val repository = app.repository
                val foodItems by repository.allFoodItems.collectAsState(initial = emptyList())

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        DaftarTopAppBar(onBackClick = { finish() })
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    DaftarScreen(
                        modifier = Modifier.padding(innerPadding),
                        foodItems = foodItems,
                        onDeleteClick = { item ->
                            lifecycleScope.launch {
                                repository.delete(item)
                                Toast.makeText(this@DaftarMakananActivity, "Makanan '${item.name}' telah dihapus/dikonsumsi", Toast.LENGTH_SHORT).show()
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
fun DaftarTopAppBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Daftar Makanan",
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
fun DaftarScreen(
    modifier: Modifier = Modifier,
    foodItems: List<FoodItem>,
    onDeleteClick: (FoodItem) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    
    val categoryFilters = listOf("Semua", "Protein", "Sayuran", "Buah", "Susu & Olahan")

    // Filter items based on selection
    val filteredItems = remember(foodItems, selectedCategoryFilter) {
        if (selectedCategoryFilter == "Semua") {
            foodItems
        } else {
            foodItems.filter { it.category == selectedCategoryFilter }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
    ) {
        // Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categoryFilters) { cat ->
                val isSelected = selectedCategoryFilter == cat
                val chipContainer = if (isSelected) GreenPrimary else Color.White
                val chipText = if (isSelected) Color.White else TextGray
                val chipBorder = if (isSelected) GreenPrimary else GrayBorder

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipContainer)
                        .border(1.dp, chipBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedCategoryFilter = cat }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = chipText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // List of items
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📥",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pantry Kosong",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Belum ada makanan tersimpan di kategori ini.",
                        color = TextGray,
                        fontSize = 13.sp,
                        textAlign = Alignment.CenterHorizontally.run { null }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    FoodItemCard(
                        foodItem = item,
                        onDeleteClick = { onDeleteClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    onDeleteClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val diffMillis = foodItem.expiryDate - now
    
    // Compute total remaining days
    val daysLeft = if (diffMillis <= 0) {
        0
    } else {
        TimeUnit.MILLISECONDS.toDays(diffMillis).toInt() + 1
    }

    // Determine status & color based on specs:
    // Merah = hampir kadaluarsa (<= 2 days)
    // Kuning = mendekati kadaluarsa (<= 5 days)
    // Hijau = masih lama (> 5 days)
    val (statusLabel, statusColor, statusBg) = when {
        daysLeft <= 0 -> Triple("Kedaluarsa", RedError, RedLight)
        daysLeft <= 2 -> Triple("Hampir Kedaluarsa", RedError, RedLight)
        daysLeft <= 5 -> Triple("Mendekati Kedaluarsa", YellowWarning, YellowLight)
        else -> Triple("Masih Lama", GreenPrimary, GreenLight)
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val formattedExpiry = remember(foodItem.expiryDate) { dateFormatter.format(foodItem.expiryDate) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(16.dp))
            .testTag("food_item_card_${foodItem.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min), // to stretch the left accent bar perfectly
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Colored Accent Stripe (Indicator)
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))

            // Main Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = foodItem.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Quantity tag
                    Box(
                        modifier = Modifier
                            .background(OffWhiteBg, shape = RoundedCornerShape(6.dp))
                            .border(1.dp, GrayBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${foodItem.quantity} ${foodItem.quantityUnit}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category icon/text
                    val catEmoji = when (foodItem.category) {
                        "Protein" -> "🍗 Protein"
                        "Sayuran" -> "🥦 Sayuran"
                        "Buah" -> "🍎 Buah"
                        else -> "🥛 Olahan"
                    }
                    Text(
                        text = catEmoji,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    
                    Text(text = "•", color = GrayBorder)
                    
                    // Specific Expiration Date
                    Text(
                        text = "Exp: $formattedExpiry",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Left Remaining Counter tag:
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(statusBg, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (daysLeft == 0) "SUDAH KADALUARSA" else "SISA $daysLeft HARI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (statusColor == YellowWarning) OrangeDeep else statusColor
                        )
                    }
                    
                    // Additional helpful status label
                    Text(
                        text = statusLabel,
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Done / Ate / Delete Button
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .background(Color(0xFFFEEBEE).copy(alpha = 0.5f), shape = CircleShape)
                    .clip(CircleShape)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eat / Delete food item",
                    tint = RedError.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
