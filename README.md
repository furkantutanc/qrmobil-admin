<h1 align="center">
  🍽️ QR Menü Yönetim Paneli — Mobil (Admin)
</h1>

<p align="center">
  <b>Restoran, Kafe ve Oteller için Modern ve Hızlı QR Menü Yönetim Uygulaması</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-FFB300?style=for-the-badge" />
</p>

---

##  Proje Hakkında
Bu uygulama, [qrmenu.otomasyonlar.net](https://qrmenu.otomasyonlar.net/) sisteminin yetkili (Admin) mobil versiyonudur. Restoran sahiplerinin menülerini diledikleri yerden anında güncelleyebilmesi, ürün eklemesi, restoran durumlarını (Açık/Kapalı) değiştirmesi ve müşteri geri bildirimlerini anlık takip etmesi için **%100 Kotlin & Jetpack Compose** kullanılarak tamamen native olarak geliştirilmiştir.

##  Öne Çıkan Özellikler

- 🔐 **Gelişmiş Kayıt & Giriş:** 5 adımlı, yüksek etkileşimli ve animasyonlu restoran kayıt akışı.
- 🍔 **Detaylı Menü Yönetimi:** Yeni kategoriler ekleme, kategorilere ürün tanımlama, fiyatları anında güncelleme veya gizleme/silme.
- 🟢 **Restoran Durum Kontrolü:** Tek tuşla işletmeyi siparişe açıp kapatma (Açık/Kapalı durumu).
- 🏷 **Restoran Profil Ayarları:** İletişim, adres ve firma bilgilerini mobilden hızlı formlar ile güncelleme.
- 💬 **Müşteri Geri Bildirimleri:** Masalardan gelen yorum, öneri ve talepleri canlı olarak takip edebilme.
- 📱 **QR Kod Jeneratörü:** İşletmeye veya özel tablolara ait QR kodların uygulama içinden otomatik üretilmesi ve tek tuşla WhatsApp vs. üzerinden paylaşılabilmesi.

##  UI/UX ve Animasyonlar
Kullanıcı deneyimi en üst düzeyde tutulmuştur. Uygulamanın her köşesinde Compose'un gelişmiş animasyon APİ'leri kullanılmıştır:
- Bouncing (yaylı) kart fizik tepkileri (`spring()` physics ile).
- Her listede (Kategoriler, Ürünler, Geri Bildirimler) *Staggered Entrance* (gecikmeli ardışık giriş) slide ve fade animasyonları.
- Zengin arka plan geçişleri (Linear ve Vertical Gradient'ler, Shimmer).
- Özelleştirilmiş dinamik SVG ve sistem ikon kullanımları.

##  Kullanılan Teknolojiler & Mimari

- **Dil:** Kotlin
- **Arayüz (UI):** Jetpack Compose (Modern Declerative UI)
- **Mimari:** MVVM (Model-View-ViewModel) + State Hoisting
- **Ağ/API Katmanı:** Retrofit2 + OkHttp + Gson Converter
- **Navigasyon:** Compose Navigation API
- **Yerel Depolama:** SharedPreferences (Token ve Session Yönetimi için)

##  Proje Dizin Yapısı
```text
com.qrmobil.admin
│
├── data/              # Model sınıfları ve veri yapısı (Kategori, Ürün vb.)
├── network/           # Retrofit instance, ApiService interface
├── navigation/        # Compose NavHost ve route tanımlamaları
├── ui/
│   ├── screens/       # Compose UI Ekranları (Dashboard, Menu, Register vb.)
│   └── theme/         # Renkler (Color.kt), Tipografi, Özel Temalandırma
└── viewmodel/         # Tüm ekranların iş mantığı (API iletişimleri ve UI State tutucuları)
```

##  Kurulum (Developer İçin)

Projeyi kendi bilgisayarınıza klonlayıp derlemek isterseniz:

1. Bu repoyu bilgisayarınıza klonlayın:
   ```bash
   git clone https://github.com/KULLANICI_ADINIZ/qrmobil-admin.git
   ```
2. **Android Studio** (Electric Eel veya Hedgehog ve üzeri tavsiye edilir) ile projeyi açın.
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. Bir Android cihaz bağlayarak veya emülatör üzerinden `Run` (Yeşil Oynat) butonuna tıklayın.

> ** API Uyarısı:** Bu proje Retrofit üzerinden canlı bir sunucuya (`qrmenu.otomasyonlar.net/api/`) test/canlı tokenlar ile çıkmaktadır. Uygulamanın doğru tepki vermesi için backend tarafının `local.properties` veya `ApiService` içine doğru yapılandırıldığına emin olun. (Gizlilik nedeniyle tokenler projeye gömülmemiştir).

##  Geliştirme Süreci Hakkında Not
Bu proje son kullanıcıyı etkileyecek detaylar (örneğin butonlardaki küçük yaylanmalar) göz önünde bulundurularak yapılmış, kod tekrarından kaçınılmış temiz MVVM yapısı ile kodlanmıştır. Modern Android geliştirme standartlarının (Jetpack Compose) kapasitesini göstermek için referans alınabilecek bir projedir.

---
`"Basit restoran girişleri yerine, kullanıcıyı karşılayan bir deneyim."`
