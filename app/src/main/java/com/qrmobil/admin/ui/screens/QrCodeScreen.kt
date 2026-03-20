package com.qrmobil.admin.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qrmobil.admin.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val restaurantName = sharedPref.getString("restaurantName", "Restoran") ?: "Restoran"
    
    fun String.toUrlSlug(): String {
        return this.trim().lowercase()
            .replace("ç", "c").replace("ğ", "g").replace("ı", "i")
            .replace("ö", "o").replace("ş", "s").replace("ü", "u")
            .replace("\\s+".toRegex(), "-").replace("[^a-z0-9-]".toRegex(), "")
    }

    val formattedName = restaurantName.toUrlSlug()
    val menuUrl = "https://qrmenu.otomasyonlar.net/restoran/view/$formattedName?qrcode=true"
    val qrBitmap = remember(menuUrl) { generateQrCode(menuUrl, 512) }

    // Entrance animations
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); visible = true }

    val cardScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "qrScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "qrAlpha"
    )

    // Scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLinePos"
    )

    // Button pulse
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Kodum", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Müşterileriniz için QR Menü",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = SectionHeader,
                modifier = Modifier.alpha(cardAlpha)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "QR Kodu okutan müşterileriniz menünüze yönlendirilir",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(cardAlpha)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // QR Card with scan animation
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.scale(cardScale).alpha(cardAlpha)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // QR with gradient border + scan line
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(3.dp)
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Kod",
                                modifier = Modifier.fillMaxSize().padding(12.dp)
                            )
                            // Scan line overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .align(Alignment.TopStart)
                                    .graphicsLayer {
                                        translationY = scanLinePosition * 234.dp.toPx()
                                    }
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                BackgroundGradientStart.copy(alpha = 0.6f),
                                                BackgroundGradientEnd.copy(alpha = 0.6f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        } else {
                            Text("QR Kod oluşturulamadı.", color = DangerRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(restaurantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SectionHeader)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(menuUrl, style = MaterialTheme.typography.bodySmall, color = TextGray, textAlign = TextAlign.Center, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsing share button
            Button(
                onClick = { 
                    if (qrBitmap != null) shareQrCode(context, qrBitmap, formattedName)
                    else android.widget.Toast.makeText(context, "QR Kod bulunamadı", android.widget.Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp).scale(pulseScale),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("📤  Galeriden Paylaş", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

fun generateQrCode(text: String, size: Int): android.graphics.Bitmap? {
    if (text.isBlank()) return null
    return try {
        val hints = java.util.EnumMap<com.google.zxing.EncodeHintType, Any>(com.google.zxing.EncodeHintType::class.java)
        hints[com.google.zxing.EncodeHintType.MARGIN] = 1
        val bitMatrix = com.google.zxing.MultiFormatWriter().encode(text, com.google.zxing.BarcodeFormat.QR_CODE, size, size, hints)
        val pixels = IntArray(size * size)
        for (y in 0 until size) { for (x in 0 until size) { pixels[y * size + x] = if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE } }
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        bitmap
    } catch (e: Exception) { null }
}

fun shareQrCode(context: Context, bitmap: android.graphics.Bitmap, iden: String) {
    try {
        val resolver = context.contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "QR_Menu_$iden.png")
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
        }
        val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "QR Kodu Paylaş"))
        }
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Hata: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
    }
}
