package com.qrmobil.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qrmobil.admin.ui.theme.BackgroundGradientStart
import com.qrmobil.admin.ui.theme.ButtonBlue
import com.qrmobil.admin.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantPreviewScreen(
    onBack: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menü Önizleme", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<-", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGradientStart)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Loop through all categories
                items(viewModel.categories) { category ->
                    val itemsForCategory = viewModel.getItemsForCategory(category.id)
                    
                    if (itemsForCategory.isNotEmpty()) {
                        Column {
                            // Category Header
                            Text(
                                text = category.name.uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                            )
                            
                            // Category Items
                            itemsForCategory.forEach { item ->
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
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "₺${item.price}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ButtonBlue,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    if (viewModel.categories.isEmpty() || viewModel.categories.all { viewModel.getItemsForCategory(it.id).isEmpty() }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Menüde gösterilecek ürün bulunmuyor.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
