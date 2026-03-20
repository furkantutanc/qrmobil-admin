package com.qrmobil.admin.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://qrmenu.otomasyonlar.net/"
    private const val TAG = "RetrofitInstance"

    // Dinamik cookie deposu
    var sessionCookie: String = ""

    // Cookie'yi otomatik ekleyen interceptor
    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .header("accept-language", "en-GB,en;q=0.9")
            .header("cache-control", "no-cache")
            .header("content-type", "application/x-www-form-urlencoded")
            .header("origin", "https://qrmenu.otomasyonlar.net")
            .header("pragma", "no-cache")
            .header("priority", "u=0, i")
            .header("referer", "https://qrmenu.otomasyonlar.net/register")
            .header("sec-ch-ua", "\"Not:A-Brand\";v=\"99\", \"Google Chrome\";v=\"145\", \"Chromium\";v=\"145\"")
            .header("sec-ch-ua-mobile", "?1")
            .header("sec-ch-ua-platform", "\"Android\"")
            .header("sec-fetch-dest", "document")
            .header("sec-fetch-mode", "navigate")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-user", "?1")
            .header("upgrade-insecure-requests", "1")
            .header("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Mobile Safari/537.36")

        // Dinamik cookie ekle (varsa)
        if (sessionCookie.isNotEmpty()) {
            builder.header("Cookie", sessionCookie)
        }

        chain.proceed(builder.method(original.method, original.body).build())
    }

    // Ham yanıtı logla
    private val rawBodyInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val body = response.peekBody(Long.MAX_VALUE)
        Log.d("RawResponse", "Kod: ${response.code} Ham Yanıt: ${body.string().take(500)}")
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val gson = GsonBuilder().setLenient().create()

    // Cookie'siz OkHttpClient — register sayfasını GET ile çekmek için
    val rawClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(rawBodyInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Register sayfasını GET ile çekerek:
     * 1. PHPSESSID cookie'sini Set-Cookie header'ından alır
     * 2. HTML içindeki _token değerini <input name="_token" value="..."> ile parse eder
     * Dönen Pair: (token, cookie)
     */
    suspend fun fetchTokenAndCookie(): Pair<String, String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val request = okhttp3.Request.Builder()
                .url("https://qrmenu.otomasyonlar.net/register")
                .header("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Mobile Safari/537.36")
                .get()
                .build()

            val response = rawClient.newCall(request).execute()
            val html = response.body?.string() ?: ""

            // Cookie'yi al
            val cookies = response.headers("Set-Cookie")
            val phpSessionId = cookies
                .mapNotNull { cookie ->
                    val match = Regex("PHPSESSID=([^;]+)").find(cookie)
                    match?.groupValues?.get(1)
                }
                .lastOrNull() ?: ""

            val cookie = if (phpSessionId.isNotEmpty()) "PHPSESSID=$phpSessionId" else ""

            // Token'ı HTML'den çek
            val tokenRegex = Regex("""name="_token"\s+value="([^"]+)"""")
            val tokenMatch = tokenRegex.find(html)
            // Bazen value önce gelir: value="..." name="_token"
            val tokenRegex2 = Regex("""value="([^"]+)"\s+name="_token"""")
            val token = tokenMatch?.groupValues?.get(1)
                ?: tokenRegex2.find(html)?.groupValues?.get(1)
                ?: ""

            Log.d(TAG, "Taze token alındı: ${token.take(20)}... | Cookie: $cookie")

            Pair(token, cookie)
        }
    }
}
