package com.qrmobil.admin.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmobil.admin.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Staggered entrance states
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var gridVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        logoVisible = true
        delay(250)
        titleVisible = true
        delay(250)
        gridVisible = true
        delay(300)
        buttonsVisible = true
    }

    // Logo spring bounce
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "logoAlpha"
    )

    // Title slide up + fade
    val titleOffset by animateFloatAsState(
        targetValue = if (titleVisible) 0f else 40f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "titleOffset"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "titleAlpha"
    )

    // Grid
    val gridScale by animateFloatAsState(
        targetValue = if (gridVisible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "gridScale"
    )
    val gridAlpha by animateFloatAsState(
        targetValue = if (gridVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "gridAlpha"
    )

    // Buttons slide up
    val buttonsOffset by animateFloatAsState(
        targetValue = if (buttonsVisible) 0f else 60f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "btnOffset"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (buttonsVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "btnAlpha"
    )

    // Subtle gradient shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerOffset"
    )

    val gradStart = lerp(BackgroundGradientStart, BackgroundGradientEnd, shimmerOffset)
    val gradEnd = lerp(BackgroundGradientEnd, BackgroundGradientStart, shimmerOffset)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(gradStart, gradEnd))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo with spring bounce
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍽️", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title with slide up
            Text(
                text = "Restoran\nYönetim Paneli",
                color = Color.White,
                style = Typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .graphicsLayer { translationY = titleOffset }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Restoranınızı kolayca yönetin",
                color = Color.White.copy(alpha = 0.8f),
                style = Typography.bodyLarge,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .graphicsLayer { translationY = titleOffset }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Grid with scale entrance
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(gridScale)
                    .alpha(gridAlpha)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row {
                        AnimatedGridItem("📊", CardIconBlue, gridVisible, 0)
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedGridItem("QR", CardIconPurple, gridVisible, 100)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        AnimatedGridItem("🪑", CardIconBlue, gridVisible, 200)
                        Spacer(modifier = Modifier.width(16.dp))
                        AnimatedGridItem("💵", CardIconPink, gridVisible, 300)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons with slide up
            Column(
                modifier = Modifier
                    .alpha(buttonsAlpha)
                    .graphicsLayer { translationY = buttonsOffset }
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Giriş Yap", color = ButtonBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Yeni Restoran Oluştur", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Güvenli ve hızlı kayıt sistemi",
                    color = Color.White.copy(alpha = 0.6f),
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnimatedGridItem(icon: String, iconColor: Color, visible: Boolean, delayMs: Int) {
    var itemVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMs.toLong())
            itemVisible = true
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (itemVisible) 0f else 180f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "gridRotation"
    )
    val scale by animateFloatAsState(
        targetValue = if (itemVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "gridItemScale"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .scale(scale)
            .graphicsLayer { rotationY = rotation }
            .background(Color.White, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 24.sp, color = iconColor)
    }
}

// Color lerp helper
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
