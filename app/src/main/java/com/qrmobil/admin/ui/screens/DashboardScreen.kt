package com.qrmobil.admin.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmobil.admin.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onMenuClick: () -> Unit,
    onInfoClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onQrClick: () -> Unit,
    onPreviewClick: () -> Unit
) {
    var isRestaurantOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val restaurantName = sharedPref.getString("restaurantName", "Restoranım") ?: "Restoranım"

    // Staggered entrance animation
    var headerVisible by remember { mutableStateOf(false) }
    val cardVisibility = remember { List(7) { mutableStateOf(false) } }

    LaunchedEffect(Unit) {
        delay(100)
        headerVisible = true
        delay(200)
        cardVisibility.forEachIndexed { index, state ->
            delay(80)
            state.value = true
        }
    }

    val headerAlpha by animateFloatAsState(
        targetValue = if (headerVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "headerAlpha"
    )
    val headerOffset by animateFloatAsState(
        targetValue = if (headerVisible) 0f else -30f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "headerOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        // Gradient Header with animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(headerAlpha)
                .graphicsLayer { translationY = headerOffset }
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    "Hoş Geldiniz 👋",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    restaurantName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Çıkış", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Status Card — animated card 0
            AnimatedDashboardCard(visible = cardVisibility[0].value, index = 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Restoran Durumu",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SectionHeader
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                if (isRestaurantOpen) "🟢 Açık — sipariş alıyor" else "🔴 Kapalı",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isRestaurantOpen) SuccessGreen else DangerRed
                            )
                        }
                        Switch(
                            checked = isRestaurantOpen,
                            onCheckedChange = { isRestaurantOpen = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedDashboardCard(visible = cardVisibility[0].value, index = 0) {
                Text(
                    "Hızlı İşlemler",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SectionHeader
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val actions = listOf(
                ActionItem("Kategoriler", "Menü kategorilerini yönet", "📂", Color(0xFF3B82F6), onCategoryClick),
                ActionItem("Menü Yönetimi", "Ürün ekle, fiyat güncelle", "🍔", Color(0xFF8B5CF6), onMenuClick),
                ActionItem("Restoran Bilgileri", "Adres, telefon, isim güncelle", "🏢", Color(0xFF0EA5E9), onInfoClick),
                ActionItem("Geri Bildirimler", "Müşteri mesajlarını gör", "💬", Color(0xFFEC4899), onFeedbackClick),
                ActionItem("QR Kodum", "Menü için QR kod oluştur & paylaş", "📱", SuccessGreen, onQrClick),
                ActionItem("Menü Önizleme", "Müşterilerin gördüğü gibi incele", "👁️", WarningOrange, onPreviewClick)
            )

            actions.forEachIndexed { index, action ->
                AnimatedDashboardCard(visible = cardVisibility[index + 1].value, index = index + 1) {
                    DashboardActionButton(action.title, action.subtitle, action.icon, action.color, action.onClick)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class ActionItem(
    val title: String,
    val subtitle: String,
    val icon: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun AnimatedDashboardCard(visible: Boolean, index: Int, content: @Composable () -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "cardAlpha$index"
    )
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardOffset$index"
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .graphicsLayer { translationY = offset }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActionButton(
    title: String,
    subtitle: String,
    icon: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    // Press scale animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    Card(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = SectionHeader,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
            Text("›", fontSize = 22.sp, color = SubtleGray, fontWeight = FontWeight.Light)
        }
    }

    // Reset press state
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
        }
    }
}
