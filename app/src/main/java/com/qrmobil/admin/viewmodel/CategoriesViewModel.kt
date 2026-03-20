package com.qrmobil.admin.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qrmobil.admin.data.Category
import com.qrmobil.admin.data.MockDatabase
import com.qrmobil.admin.network.RetrofitInstance
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "CategoriesVM"
    }

    private val _categories = mutableStateListOf<Category>()
    val categories: List<Category> get() = _categories

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val sharedPref = application.getSharedPreferences("user_prefs", 0)
    private val restoranId: Int get() = sharedPref.getInt("restoranId", -1)

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // API'den çek — restoranId ne olursa olsun dene
                Log.d("CategoriesVM_RAW", "restoranId = $restoranId (SharedPreferences'tan)")
                run {

                    // Session al
                    val (_, freshCookie) = RetrofitInstance.fetchTokenAndCookie()
                    if (freshCookie.isNotEmpty()) {
                        RetrofitInstance.sessionCookie = freshCookie
                    }

                    val response = RetrofitInstance.api.getCategories(restoranId)

                    // Ham yanıtı filtrelemeden tam yazdır
                    val rawBody: String
                    if (response.isSuccessful) {
                        rawBody = response.body()?.string() ?: "(boş body)"
                    } else {
                        rawBody = response.errorBody()?.string() ?: "(boş errorBody)"
                    }

                    Log.d("CategoriesVM_RAW", "═══════════════════════════════════════")
                    Log.d("CategoriesVM_RAW", "HTTP KOD: ${response.code()}")
                    Log.d("CategoriesVM_RAW", "URL: ${response.raw().request.url}")
                    Log.d("CategoriesVM_RAW", "HEADERS: ${response.headers()}")
                    // Uzun yanıtları parça parça logla (Logcat 4000 karakter sınırı var)
                    val chunks = rawBody.chunked(3000)
                    chunks.forEachIndexed { i, chunk ->
                        Log.d("CategoriesVM_RAW", "YANIT [${i+1}/${chunks.size}]: $chunk")
                    }
                    Log.d("CategoriesVM_RAW", "═══════════════════════════════════════")

                    if (response.isSuccessful && rawBody.isNotEmpty() && rawBody != "(boş body)") {
                        val parsed = parseCategories(rawBody)
                        if (parsed.isNotEmpty()) {
                            _categories.clear()
                            _categories.addAll(parsed)
                            // MockDatabase'i de güncelle (menü yönetimi için)
                            MockDatabase.categories.clear()
                            MockDatabase.categories.addAll(parsed)
                            Log.d(TAG, "✅ ${parsed.size} kategori yüklendi")
                            isLoading = false
                            return@launch
                        } else {
                            Log.w(TAG, "⚠️ Yanıt geldi ama parse edilemedi!")
                        }
                    }
                }

                // API çalışmazsa veya restoran_id yoksa — yerel verilerden yükle
                Log.d(TAG, "API'den veri alınamadı, yerel veriler kullanılıyor")
                _categories.clear()
                _categories.addAll(MockDatabase.categories)

                if (_categories.isEmpty()) {
                    errorMessage = "Henüz kategori eklenmemiş"
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Kategori çekme hatası: ${e.message}", e)
                // Hata olsa bile yerel verileri göster
                _categories.clear()
                _categories.addAll(MockDatabase.categories)
                if (_categories.isEmpty()) {
                    errorMessage = "Henüz kategori eklenmemiş"
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun addCategory(name: String) {
        val newCategory = Category(id = UUID.randomUUID().toString(), name = name)
        MockDatabase.categories.add(newCategory)
        _categories.add(newCategory)

        // API'ye de göndermeyi dene (arka planda)
        if (restoranId > 0) {
            viewModelScope.launch {
                try {
                    val (token, cookie) = RetrofitInstance.fetchTokenAndCookie()
                    RetrofitInstance.sessionCookie = cookie
                    val response = RetrofitInstance.api.addCategory(token, restoranId, name)
                    Log.d(TAG, "Kategori ekleme yanıtı: ${response.code()}")
                } catch (e: Exception) {
                    Log.w(TAG, "API'ye kategori eklenemedi (yerel eklendi): ${e.message}")
                }
            }
        }
    }

    private fun parseCategories(rawBody: String): List<Category> {
        val result = mutableListOf<Category>()
        try {
            val trimmed = rawBody.trim()
            when {
                // JSON Array: [{"id":1,"name":"..."}, ...]
                trimmed.startsWith("[") -> {
                    val arr = JSONArray(trimmed)
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val id = obj.optString("id", obj.optString("kategori_id", "$i"))
                        val name = obj.optString("name", obj.optString("kategoriAdi", obj.optString("kategori_adi", "")))
                        if (name.isNotBlank()) {
                            result.add(Category(id = id, name = name))
                        }
                    }
                }
                // JSON Object: {"success":true, "data":[...]}
                trimmed.startsWith("{") -> {
                    val obj = JSONObject(trimmed)
                    val data = obj.optJSONArray("data") ?: obj.optJSONArray("categories")
                    if (data != null) {
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            val id = item.optString("id", item.optString("kategori_id", "$i"))
                            val name = item.optString("name", item.optString("kategoriAdi", item.optString("kategori_adi", "")))
                            if (name.isNotBlank()) {
                                result.add(Category(id = id, name = name))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Kategori JSON parse hatası: ${e.message}")
        }
        return result
    }
}
