package com.qrmobil.admin.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qrmobil.admin.network.RetrofitInstance
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    var step by mutableStateOf(1)
        private set

    // Step 1: Restaurant Info
    var restaurantName by mutableStateOf("")
    var slogan by mutableStateOf("")

    // Step 2: Address
    var city by mutableStateOf("")
    var district by mutableStateOf("")
    var fullAddress by mutableStateOf("")

    // Step 3: Working Hours
    var openTime by mutableStateOf("09:00")
    var closeTime by mutableStateOf("22:00")

    // Step 4: Contact
    var phone by mutableStateOf("")
    var email by mutableStateOf("")

    // Step 5: Security
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // API state
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    companion object {
        private const val TAG = "RegistrationVM"
        // Arkadaşın örneğindeki sabit token
        private const val HARDCODED_TOKEN = "7846f88867d8af63c00fea14360ec9923a64e8e45b30ee73aec78fabdb35a908"
    }

    fun nextStep() {
        if (canGoNext() && step < 5) step++
    }

    fun previousStep() {
        if (step > 1) step--
    }

    fun canGoNext(): Boolean {
        return when (step) {
            1 -> restaurantName.isNotBlank()
            2 -> city.isNotBlank() && district.isNotBlank() && fullAddress.isNotBlank()
            3 -> openTime.isNotBlank() && closeTime.isNotBlank()
            4 -> phone.isNotBlank() && email.contains("@")
            5 -> password.isNotBlank() && password == confirmPassword
            else -> false
        }
    }

    fun submitRegistration(context: Context, onSuccess: () -> Unit) {
        if (!canGoNext()) return

        isLoading = true
        errorMessage = null

        // Telefon numarasını temizle: başındaki 0'ı kaldır, sadece rakam bırak
        val cleanPhone = phone.trim().replace("\\s+".toRegex(), "").let {
            if (it.startsWith("0")) it.substring(1) else it
        }

        viewModelScope.launch {
            try {
                // 1. Taze token ve cookie al
                Log.d(TAG, "Taze token alınıyor...")
                val (freshToken, freshCookie) = RetrofitInstance.fetchTokenAndCookie()

                if (freshToken.isEmpty()) {
                    Log.e(TAG, "❌ Token alınamadı!")
                    errorMessage = "Sunucudan token alınamadı. Tekrar deneyin."
                    isLoading = false
                    return@launch
                }

                // Cookie'yi Retrofit interceptor'a set et
                RetrofitInstance.sessionCookie = freshCookie
                Log.d(TAG, "Kayıt isteği gönderiliyor: $restaurantName | Token: ${freshToken.take(20)}... | Cookie: $freshCookie")

                // 2. Kayıt isteğini gönder
                val response = RetrofitInstance.api.register(
                    token = freshToken,
                    restoranAdi = restaurantName,
                    restoranSlogani = slogan,
                    restoranAdresi = fullAddress,
                    restoranSehir = city,
                    restoranIlce = district,
                    restoranMail = email,
                    restoranTelefon = cleanPhone,
                    restoranSifre = password,
                    restoranSifreTekrar = confirmPassword,
                    // Haftanın 7 günü — aynı çalışma saatleri
                    restoranGunPztStart = openTime,
                    restoranGunPztEnd = closeTime,
                    restoranGunSalStart = openTime,
                    restoranGunSalEnd = closeTime,
                    restoranGunCarStart = openTime,
                    restoranGunCarEnd = closeTime,
                    restoranGunPerStart = openTime,
                    restoranGunPerEnd = closeTime,
                    restoranGunCumStart = openTime,
                    restoranGunCumEnd = closeTime,
                    restoranGunCtesiStart = openTime,
                    restoranGunCtesiEnd = closeTime,
                    restoranGunPazStart = openTime,
                    restoranGunPazEnd = closeTime
                )

                // Ham yanıtı oku
                val rawBody = if (response.isSuccessful) {
                    response.body()?.string() ?: ""
                } else {
                    response.errorBody()?.string() ?: ""
                }

                Log.d(TAG, "Sunucu Yanıtı (Kod: ${response.code()}): $rawBody")

                // HTTP 200 döndüyse kayıt başarılıdır (sunucu kaydı yapıyor)
                if (response.code() == 200) {
                    var restoranId = -1

                    // JSON'dan restoran_id çekmeyi dene (opsiyonel)
                    if (rawBody.trimStart().startsWith("{")) {
                        try {
                            val gson = com.google.gson.Gson()
                            val result = gson.fromJson(rawBody, com.qrmobil.admin.network.RegistrationResponse::class.java)
                            restoranId = result?.data?.restoranId ?: -1
                        } catch (_: Exception) { }
                    }

                    Log.d(TAG, "✅ Kayıt başarılı! restoran_id: $restoranId")

                    // SharedPreferences'a kaydet
                    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("restaurantName", restaurantName)
                        putString("phone", cleanPhone)
                        putString("email", email)
                        putString("password", password)
                        putString("city", city)
                        putString("district", district)
                        putString("address", fullAddress)
                        putString("slogan", slogan)
                        if (restoranId > 0) putInt("restoranId", restoranId)
                        apply()
                    }
                    onSuccess()
                } else {
                    Log.e(TAG, "❌ Kayıt başarısız! Kod: ${response.code()} Yanıt: $rawBody")
                    errorMessage = "Kayıt başarısız (Kod: ${response.code()}). Tekrar deneyin."
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Bağlantı hatası: ${e.message}", e)
                errorMessage = "Bağlantı hatası: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
