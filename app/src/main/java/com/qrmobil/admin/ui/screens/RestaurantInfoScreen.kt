package com.qrmobil.admin.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qrmobil.admin.ui.theme.*
import com.qrmobil.admin.viewmodel.RestaurantInfoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfoScreen(
    onBack: () -> Unit,
    viewModel: RestaurantInfoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); visible = true }

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "infoAlpha"
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 30f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "infoOffset"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restoran Bilgileri", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
                .alpha(contentAlpha)
                .graphicsLayer { translationY = contentOffset },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(alpha = 0.08f))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Text("ℹ️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Restoran bilgilerinizi buradan güncelleyebilirsiniz.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }

            // Form card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.restaurantName,
                        onValueChange = { viewModel.restaurantName = it },
                        label = { Text("🏪  Restoran Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("📧  E-posta Adresi") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.phone,
                        onValueChange = { viewModel.phone = it },
                        label = { Text("📞  Telefon Numarası") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("🔑  Yeni Şifre (Boş bırakırsanız değişmez)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveInfo {
                        Toast.makeText(context, "Bilgiler başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Değişiklikleri Kaydet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
