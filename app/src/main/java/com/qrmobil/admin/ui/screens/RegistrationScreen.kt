package com.qrmobil.admin.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmobil.admin.ui.theme.*
import com.qrmobil.admin.viewmodel.RegistrationViewModel

import androidx.compose.ui.platform.LocalContext

// Returns 0=empty, 1=Zayıf, 2=Orta, 3=İyi, 4=Güçlü
fun getPasswordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return score
}

private val stepIcons = listOf("🏠", "📍", "🕐", "📞", "🔐")
private val stepTitles = listOf("Restoran", "Adres", "Saatler", "İletişim", "Güvenlik")

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onRegistrationComplete: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (viewModel.step > 1) viewModel.previousStep()
                            else onBackToLogin()
                        }
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_media_previous),
                            contentDescription = "Geri",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Adım ${viewModel.step} / 5",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Yeni Restoran Oluştur",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Step Progress Indicator
                val infiniteTransition = rememberInfiniteTransition(label = "stepPulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = EaseInOutCubic),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 1..5) {
                        val isActive = i == viewModel.step
                        val stepScale by animateFloatAsState(
                            targetValue = if (i <= viewModel.step) 1f else 0.85f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "stepScale$i"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .scale(if (isActive) pulseScale * stepScale else stepScale)
                                    .clip(CircleShape)
                                    .background(
                                        if (i <= viewModel.step)
                                            Color.White
                                        else
                                            Color.White.copy(alpha = 0.25f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stepIcons[i - 1],
                                    fontSize = 18.sp,
                                    color = if (i <= viewModel.step) BackgroundGradientStart else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                stepTitles[i - 1],
                                style = MaterialTheme.typography.labelSmall,
                                color = if (i <= viewModel.step) Color.White else Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    AnimatedContent(
                        targetState = viewModel.step,
                        label = "registration_steps",
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        }
                    ) { targetStep ->
                        when (targetStep) {
                            1 -> Step1RestaurantInfo(viewModel)
                            2 -> Step2Address(viewModel)
                            3 -> Step3WorkingHours(viewModel)
                            4 -> Step4ContactInfo(viewModel)
                            5 -> Step5Security(viewModel)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (viewModel.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = DangerRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Next / Submit Button
            val isStepValid = viewModel.canGoNext() && !viewModel.isLoading
            Button(
                onClick = {
                    if (viewModel.step < 5) {
                        viewModel.nextStep()
                    } else {
                        viewModel.submitRegistration(context) {
                            onRegistrationComplete()
                        }
                    }
                },
                enabled = isStepValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    disabledContainerColor = SubtleGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (viewModel.step < 5) "İleri →" else "Kayıt Ol ✓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1RestaurantInfo(viewModel: RegistrationViewModel) {
    Column {
        Text("🏠 Restoran Bilgileri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SectionHeader)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Restoranınızın adını ve varsa sloganını girin.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = viewModel.restaurantName,
            onValueChange = { viewModel.restaurantName = it },
            label = { Text("Restoran Adı") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.slogan,
            onValueChange = { viewModel.slogan = it },
            label = { Text("Slogan (Opsiyonel)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Address(viewModel: RegistrationViewModel) {
    Column {
        Text("📍 Adres Bilgileri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SectionHeader)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Müşterilerin sizi bulabilmesi için adresinizi girin.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = viewModel.city,
                onValueChange = { viewModel.city = it },
                label = { Text("İl") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = viewModel.district,
                onValueChange = { viewModel.district = it },
                label = { Text("İlçe") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.fullAddress,
            onValueChange = { viewModel.fullAddress = it },
            label = { Text("Açık Adres") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step3WorkingHours(viewModel: RegistrationViewModel) {
    Column {
        Text("🕐 Çalışma Saatleri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SectionHeader)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Restoranınızın çalışma saatlerini belirleyin.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = viewModel.openTime,
            onValueChange = { viewModel.openTime = it },
            label = { Text("Açılış Saati") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.closeTime,
            onValueChange = { viewModel.closeTime = it },
            label = { Text("Kapanış Saati") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step4ContactInfo(viewModel: RegistrationViewModel) {
    Column {
        Text("📞 İletişim Bilgileri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SectionHeader)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Sizinle ve müşterilerinizle iletişim için.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = viewModel.phone,
            onValueChange = { viewModel.phone = it },
            label = { Text("Telefon Numarası") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("E-posta Adresi") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step5Security(viewModel: RegistrationViewModel) {
    val strength = getPasswordStrength(viewModel.password)
    val strengthLabel = when (strength) {
        0 -> ""
        1 -> "Zayıf"
        2 -> "Orta"
        3 -> "İyi"
        4 -> "Güçlü"
        else -> ""
    }
    val strengthColor = when (strength) {
        1 -> DangerRed
        2 -> WarningOrange
        3 -> Color(0xFFFDD835)
        4 -> SuccessGreen
        else -> Color.Transparent
    }

    Column {
        Text("🔐 Şifre Belirleme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SectionHeader)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Hesabınızı güvene almak için güçlü bir şifre seçin.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Strength Bar
        if (viewModel.password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Şifre güçlülüğü:", style = MaterialTheme.typography.bodySmall, color = TextGray)
                Text(strengthLabel, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = strengthColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..4) {
                    val filled = i <= strength
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                color = if (filled) strengthColor else SubtleGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Güçlü bir şifre için: 8+ karakter, büyük/küçük harf, rakam ve özel karakter kullanın.",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            label = { Text("Şifre (Tekrar)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = viewModel.confirmPassword.isNotEmpty() && viewModel.password != viewModel.confirmPassword
        )
        if (viewModel.confirmPassword.isNotEmpty() && viewModel.password != viewModel.confirmPassword) {
            Text(
                "Şifreler eşleşmiyor.",
                color = DangerRed,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
