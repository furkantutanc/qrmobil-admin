package com.qrmobil.admin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qrmobil.admin.ui.theme.*
import com.qrmobil.admin.viewmodel.CategoriesViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    viewModel: CategoriesViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategoriler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("◀", fontSize = 18.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = SectionHeader
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = ButtonBlue
            ) {
                Text("+", color = Color.White, fontSize = 24.sp)
            }
        },
        containerColor = SurfaceGray
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ButtonBlue
                    )
                }
                viewModel.categories.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📂", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            viewModel.errorMessage ?: "Henüz kategori eklenmemiş",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Sağ alttaki + butonuna basarak yeni kategori ekleyebilirsiniz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(viewModel.categories) { index, category ->
                            // Staggered entrance
                            var itemVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                delay(index * 60L)
                                itemVisible = true
                            }
                            val itemAlpha by animateFloatAsState(
                                targetValue = if (itemVisible) 1f else 0f,
                                animationSpec = tween(400),
                                label = "catAlpha$index"
                            )
                            val itemOffset by animateFloatAsState(
                                targetValue = if (itemVisible) 0f else 40f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "catOffset$index"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(itemAlpha)
                                    .graphicsLayer { translationY = itemOffset },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📂", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = SectionHeader
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Yeni Kategori Ekle", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Kategori Adı (Örn: İçecekler)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                viewModel.addCategory(newCategoryName)
                                newCategoryName = ""
                                showDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                    ) { Text("Ekle") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("İptal") }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
