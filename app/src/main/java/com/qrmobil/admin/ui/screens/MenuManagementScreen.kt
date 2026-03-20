package com.qrmobil.admin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qrmobil.admin.data.Category
import com.qrmobil.admin.data.MenuItem
import com.qrmobil.admin.viewmodel.MenuViewModel
import com.qrmobil.admin.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    onBack: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf<Category?>(viewModel.categories.firstOrNull()) }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf<MenuItem?>(null) }
    var editItemName by remember { mutableStateOf("") }
    var editItemPrice by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.categories) {
        if (selectedCategory == null && viewModel.categories.isNotEmpty()) {
            selectedCategory = viewModel.categories.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menü Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("◀", fontSize = 18.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = SectionHeader
                )
            )
        },
        floatingActionButton = {
            if (selectedCategory != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = ButtonBlue,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // Animated Category Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.categories) { category ->
                    val isSelected = selectedCategory?.id == category.id
                    val chipScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "chipScale"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(category.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.scale(chipScale),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ButtonBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            HorizontalDivider(color = SubtleGray, thickness = 1.dp)

            selectedCategory?.let { category ->
                val itemsForCat = viewModel.getItemsForCategory(category.id)
                if (itemsForCat.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍽️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Bu kategoride henüz ürün yok.", color = TextGray, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+ butonuna basarak ürün ekleyebilirsiniz.", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(itemsForCat) { index, item ->
                            // Staggered entrance
                            var itemVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                delay(index * 60L)
                                itemVisible = true
                            }
                            val itemAlpha by animateFloatAsState(
                                targetValue = if (itemVisible) 1f else 0f,
                                animationSpec = tween(400),
                                label = "menuAlpha$index"
                            )
                            val itemOffset by animateFloatAsState(
                                targetValue = if (itemVisible) 0f else 40f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "menuOffset$index"
                            )

                            // Press scale
                            var pressed by remember { mutableStateOf(false) }
                            val pressScale by animateFloatAsState(
                                targetValue = if (pressed) 0.96f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "pressScale$index"
                            )
                            LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(itemAlpha)
                                    .graphicsLayer { translationY = itemOffset }
                                    .scale(pressScale)
                                    .clickable {
                                        pressed = true
                                        showEditDialog = item
                                        editItemName = item.name
                                        editItemPrice = item.price.toString()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            item.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = SectionHeader
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = ButtonBlue.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            "₺${item.price}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ButtonBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Add Dialog
            if (showAddDialog && selectedCategory != null) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("${selectedCategory!!.name} — Yeni Ürün", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = newItemName, onValueChange = { newItemName = it },
                                label = { Text("Ürün Adı (Örn: Kahve)") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = newItemPrice, onValueChange = { newItemPrice = it },
                                label = { Text("Fiyat (Örn: 50.0)") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val price = newItemPrice.toDoubleOrNull()
                                if (newItemName.isNotBlank() && price != null) {
                                    viewModel.addMenuItem(selectedCategory!!.id, newItemName, price)
                                    newItemName = ""; newItemPrice = ""; showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Ekle") }
                    },
                    dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("İptal", color = TextGray) } },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Edit Dialog
            if (showEditDialog != null) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = null },
                    title = { Text("Ürünü Düzenle", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = editItemName, onValueChange = { editItemName = it },
                                label = { Text("Ürün Adı") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = editItemPrice, onValueChange = { editItemPrice = it },
                                label = { Text("Fiyat") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val price = editItemPrice.toDoubleOrNull()
                                if (editItemName.isNotBlank() && price != null) {
                                    viewModel.updateMenuItem(showEditDialog!!.id, editItemName, price)
                                    showEditDialog = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Kaydet") }
                    },
                    dismissButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = { viewModel.deleteMenuItem(showEditDialog!!.id); showEditDialog = null },
                                colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                            ) { Text("Sil") }
                            TextButton(onClick = { showEditDialog = null }) { Text("İptal", color = TextGray) }
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}
