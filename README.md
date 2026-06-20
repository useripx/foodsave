<p align="center">
  <img src="https://img.icons8.com/3d-fluency/94/food-bar.png" alt="FoodSave Logo" width="94" height="94"/>
</p>

<h1 align="center">🍱 FoodSave</h1>

<p align="center">
  <strong>Track your food inventory, minimize waste, and never let food expire again.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform Android"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge" alt="Min SDK 24"/>
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=for-the-badge" alt="Target SDK 36"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Gradle-9.3.1-02303A?style=flat-square&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/AGP-9.1.1-02303A?style=flat-square&logo=android&logoColor=white" alt="AGP"/>
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin Version"/>
  <img src="https://img.shields.io/badge/Firebase-AI-FFCA28?style=flat-square&logo=firebase&logoColor=black" alt="Firebase AI"/>
  <img src="https://img.shields.io/badge/Room-2.7.0-4285F4?style=flat-square" alt="Room"/>
  <img src="https://img.shields.io/badge/Retrofit-2.12.0-48B983?style=flat-square" alt="Retrofit"/>
  <img src="https://img.shields.io/github/license/useripx/foodsave?style=flat-square" alt="License"/>
</p>

---

## 📖 Tentang

**FoodSave** adalah aplikasi Android yang membantu pengguna melacak inventaris makanan dan meminimalkan food waste dengan fitur peringatan kedaluwarsa serta pelacakan yang intuitif. Didukung oleh **Gemini AI** melalui Firebase untuk memberikan rekomendasi cerdas.

---

## ✨ Fitur Utama

- 🗂️ **Manajemen Inventaris Makanan** — Catat dan kelola stok makanan dengan mudah
- ⏰ **Peringatan Kedaluwarsa** — Notifikasi otomatis sebelum makanan expired
- 🤖 **AI-Powered** — Rekomendasi cerdas menggunakan Gemini AI via Firebase
- 💾 **Penyimpanan Lokal** — Data tersimpan aman menggunakan Room Database
- 🎨 **Material Design 3** — UI modern dengan Jetpack Compose & Material You

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|----------|-----------|
| **Bahasa** | Kotlin 2.2.10 |
| **UI Framework** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM (ViewModel + Compose) |
| **Database** | Room 2.7.0 |
| **Networking** | Retrofit 2.12.0 + OkHttp 4.10.0 + Moshi |
| **AI / ML** | Firebase AI (Gemini API) |
| **Async** | Kotlin Coroutines 1.10.2 |
| **Build Tool** | Gradle 9.3.1 + AGP 9.1.1 |
| **Code Gen** | KSP 2.3.5 |
| **Testing** | JUnit 4, Espresso, Robolectric, Roborazzi |

---

## 📋 Prasyarat

Sebelum menjalankan project ini, pastikan sudah terinstall:

- [Android Studio](https://developer.android.com/studio) (versi terbaru disarankan)
- **JDK 11** atau lebih baru
- **Android SDK** dengan API Level 36
- **Gemini API Key** — dapatkan di [Google AI Studio](https://aistudio.google.com/)

---

## 🚀 Cara Menjalankan

### 1. Clone Repository

```bash
git clone https://github.com/useripx/foodsave.git
cd foodsave
```

### 2. Konfigurasi Environment

Buat file `.env` di root project berdasarkan template `.env.example`:

```bash
cp .env.example .env
```

Edit file `.env` dan masukkan API key:

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

### 3. Buka di Android Studio

1. Buka **Android Studio**
2. Pilih **File → Open** → navigasi ke folder `foodsave`
3. Tunggu proses Gradle sync selesai

### 4. Jalankan Aplikasi

1. Sambungkan device Android atau jalankan **Emulator**
2. Klik tombol **▶ Run** atau tekan `Shift + F10`
3. Pilih target device dan tunggu instalasi selesai

### 5. Build APK (Opsional)

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (membutuhkan signing config)
./gradlew assembleRelease
```

---

## 📁 Struktur Project

```
foodsave/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Source code (Kotlin)
│   │   │   ├── res/           # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/              # Unit tests
│   │   └── androidTest/       # Instrumented tests
│   └── build.gradle.kts       # App-level build config
├── gradle/
│   ├── libs.versions.toml     # Version catalog
│   └── wrapper/               # Gradle wrapper
├── .env.example               # Template environment variables
├── build.gradle.kts           # Project-level build config
├── settings.gradle.kts        # Gradle settings
├── PERBAIKAN.md               # Dokumentasi perbaikan
└── README.md                  # Dokumentasi project (file ini)
```

---

## 🧪 Testing

```bash
# Jalankan unit tests
./gradlew test

# Jalankan instrumented tests (perlu device/emulator)
./gradlew connectedAndroidTest

# Jalankan screenshot tests (Roborazzi)
./gradlew verifyRoborazziDebug
```

---

## 🔧 Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Gradle version error | Pastikan `gradle-wrapper.properties` menggunakan `gradle-9.3.1-bin.zip` |
| Keystore not found | Debug build sudah dikonfigurasi untuk menggunakan default Android debug keystore |
| DNS / Network error saat sync | Cek koneksi internet, jalankan `ipconfig /flushdns` |
| GEMINI_API_KEY error | Buat file `.env` dan isi dengan API key valid |

---

## 🌿 Branching Strategy

| Branch | Deskripsi |
|--------|-----------|
| `main` | Branch utama, kode stabil dan siap release |
| `develop` | Branch pengembangan, fitur baru dikerjakan di sini |

---

## 🤝 Kontribusi

1. Fork repository ini
2. Buat branch fitur (`git checkout -b feature/fitur-baru`)
3. Commit perubahan (`git commit -m 'Menambahkan fitur baru'`)
4. Push ke branch (`git push origin feature/fitur-baru`)
5. Buat Pull Request ke branch `develop`

---

## 📄 Lisensi

Project ini dibuat untuk keperluan edukasi dan pengembangan.

---

<p align="center">
  Made with ❤️ using Kotlin & Jetpack Compose
</p>
