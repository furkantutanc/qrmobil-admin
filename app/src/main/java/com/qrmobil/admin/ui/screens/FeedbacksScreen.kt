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
import com.qrmobil.admin.viewmodel.FeedbacksViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbacksScreen(
    onBack: () -> Unit,
    viewModel: FeedbacksViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Geri Bildirimler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("◀", fontSize = 18.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = SectionHeader
                )
            )
        },
        containerColor = SurfaceGray
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.feedbacks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("💬", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Henüz geri bildirim yok",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Müşterilerinizden gelen geri bildirimler burada görünecek.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(viewModel.feedbacks) { index, feedback ->
                        // Staggered entrance
                        var itemVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(index * 70L)
                            itemVisible = true
                        }
                        val itemAlpha by animateFloatAsState(
                            targetValue = if (itemVisible) 1f else 0f,
                            animationSpec = tween(400),
                            label = "fbAlpha$index"
                        )
                        val itemOffset by animateFloatAsState(
                            targetValue = if (itemVisible) 0f else 40f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "fbOffset$index"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(itemAlpha)
                                .graphicsLayer { translationY = itemOffset },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = CardIconPurple.copy(alpha = 0.1f),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                Text(
                                                    feedback.senderName.take(1).uppercase(),
                                                    fontWeight = FontWeight.Bold,
                                                    color = CardIconPurple,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(feedback.senderName, fontWeight = FontWeight.Bold, color = SectionHeader)
                                    }
                                    Text(feedback.date, style = MaterialTheme.typography.bodySmall, color = TextGray)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(feedback.message, style = MaterialTheme.typography.bodyMedium, color = TextDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
