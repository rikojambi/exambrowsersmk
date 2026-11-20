# ExamBrowser SMKN 6 Muaro Jambi

Aplikasi Android ringan yang berfungsi sebagai **pintu masuk ke CBT sekolah** melalui Wi-Fi lokal. Alamat server CBT **dimasukkan manual oleh siswa**, bukan ditanam permanen di aplikasi.

- **Penyusun:** Riko Hardona
- **Sekolah:** SMKN 6 Muaro Jambi
- **Alamat CBT:** dimasukkan siswa saat aplikasi dibuka, misalnya `http://192.168.10.1/cbt`
- **Jaringan:** Wi-Fi lokal sekolah, tanpa internet
- **Server CBT:** PHP pada Laragon Windows (tidak diubah sama sekali)

## 1. Fungsi aplikasi

Aplikasi ini **hanya**:

1. Menampilkan halaman awal berisi nama aplikasi, nama sekolah, kolom alamat IP/URL CBT, tombol **HUBUNGKAN**, dan indikator status koneksi.
2. Memvalidasi alamat yang dimasukkan siswa (IP lokal atau hostname lokal, HTTP/HTTPS, tanpa username/password).
3. Memeriksa koneksi Wi-Fi dan keterjangkauan server CBT sebelum membuka halaman.
4. Membuka halaman CBT di dalam WebView tanpa address bar/toolbar browser.
5. Menyimpan **alamat CBT terakhir** di penyimpanan lokal aplikasi dan menampilkannya lagi sebagai isian awal saat aplikasi dibuka kembali.
6. Membatasi navigasi WebView hanya ke **origin** server CBT yang sudah divalidasi (skema + host + port). Semua path di origin tersebut tetap diizinkan agar login, soal, gambar, file, timer, dan pengiriman jawaban tidak terganggu.
7. Memblokir screenshot, screen recording, dan thumbnail *recent apps* standar Android melalui `FLAG_SECURE`.
8. Menyembunyikan status bar dan navigation bar (immersive fullscreen).
9. Mendeteksi saat aplikasi sempat tidak aktif, lalu menampilkan peringatan dan mencatat kejadian **di memori selama sesi** (tanpa database, tanpa file, tanpa pengiriman data).
10. Menyediakan tombol **Ganti Server** dengan konfirmasi untuk kembali ke halaman awal.

Aplikasi ini **bukan**:

- pembuat soal, pengelola nilai, sistem ujian baru, LMS, atau pengganti CBT sekolah;
- sistem login baru — login, soal, jawaban, timer, dan penilaian tetap sepenuhnya di CBT;
- penyimpan username/password CBT — aplikasi **tidak menyimpan kredensial apa pun**.

## 2. Isi proyek

```
exambrowser/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/id/sch/smkn6muarojambi/exambrowser/
│       │   ├── CbtAddress.kt        # validasi & normalisasi alamat dengan URI
│       │   ├── CbtSession.kt        # origin aktif + pembatasan navigasi WebView
│       │   ├── NetworkChecker.kt    # cek Wi-Fi + socket ke host:port CBT
│       │   ├── SessionMonitor.kt    # catatan sesi di memori
│       │   ├── ExamApplication.kt   # deteksi background
│       │   └── MainActivity.kt      # halaman input, WebView, FLAG_SECURE
│       └── res/                     # layout, warna, teks, ikon, network config
├── gradle/                          # wrapper Gradle
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
├── PANDUAN_INSTALLASI.md
├── PANDUAN_PENGGUNAAN.md
├── PANDUAN_IPHONE.md
└── UJI_COBA.md
```

Dependensi hanya plugin Android Gradle, plugin Kotlin Android, dan Kotlin stdlib. Tidak ada framework UI, database lokal, backend, atau API tambahan.

`Config.kt` dan `UrlPolicy.kt` **sudah dihapus**; jangan dicari lagi di proyek ini.

## 3. Persyaratan instalasi (untuk membangun)

- Android Studio disarankan versi **Android Studio Ladybug (2024.2.1)** atau lebih baru.
- **JDK 17** (biasanya sudah termasuk di Android Studio → *bundled JBR*).
- Android SDK Platform **API 35** dan Android SDK Build-Tools **35.x**.
- Koneksi internet **hanya saat pertama kali** untuk mengunduh Gradle/plugin. Setelah itu build bisa offline.
- Perangkat Android dengan **minSdk 21 (Android 5.0)** atau lebih baru dan WebView terpasang.

> Untuk menjalankan aplikasi di HP siswa, yang dibutuhkan hanyalah APK dan Wi-Fi sekolah. Android Studio tidak perlu dipasang di HP siswa.

## 4. Cara membuka proyek di Android Studio

1. Buka Android Studio → **File → Open…**
2. Pilih folder `C:\laragon\www\exambrowser`.
3. Tunggu *Gradle sync* selesai.
4. Jika Android Studio meminta SDK, pasang **Android SDK Platform 35** dan **Build-Tools 35.x** melalui *SDK Manager*.
5. Pilih perangkat/emulator lalu tekan **Run** (tombol ▶).

## 5. Alamat CBT dan validasinya

Tidak ada URL CBT yang ditanam permanen. Siswa memasukkan alamat yang diberikan guru, contoh:

```
http://192.168.10.1/cbt
```

Aturan validasi di `CbtAddress.kt`:

- input tidak boleh kosong;
- jika tidak ada skema, `http://` ditambahkan otomatis (misalnya `192.168.10.1/cbt` menjadi `http://192.168.10.1/cbt`);
- hanya `http://` dan `https://` yang diterima;
- IP lokal dan hostname lokal diterima;
- port boleh ditulis eksplisit, misalnya `http://cbt.sekolah.local:8080/cbt`;
- URL yang mengandung username/password ditolak;
- host, port, dan path diparsing dengan `java.net.URI`, bukan dengan `contains`;
- segmen path `..` ditolak.

Alamat yang berhasil digunakan disimpan di `SharedPreferences` dengan nama file `exambrowser` dan kunci `cbt_address`. Hanya **alamatnya** yang disimpan, bukan username/password. Saat aplikasi dibuka kembali, alamat terakhir menjadi isian awal. Siswa tetap bisa mengubahnya, baik langsung di kolom maupun lewat tombol **Ganti Server** (dengan konfirmasi).

### Pembatasan navigasi

`CbtSession.isAllowed()` membandingkan `scheme + host + effective port` dari setiap URL yang dimuat WebView dengan alamat yang sudah divalidasi:

- **diizinkan:** semua path, query, redirect, aset gambar/JS/CSS, dan file di server CBT yang sama;
- **diblokir:** situs eksternal, skema `mailto:`, `tel:`, `file:`, `javascript:`, dan alamat dengan host/port berbeda;
- `about:blank` diizinkan untuk membersihkan WebView saat tombol **Ganti Server** ditekan.

Karena alamat CBT bisa arbitrary, `network_security_config.xml` mengizinkan HTTP (`cleartextTrafficPermitted="true"`) secara umum. Pembatasan keamanan tetap dilakukan di level WebView melalui origin yang divalidasi. `MIXED_CONTENT_COMPATIBILITY_MODE` dipakai agar halaman HTTPS dengan sebagian aset HTTP lokal tidak langsung rusak.

## 6. Cara membuat APK

### Menggunakan Android Studio

- **APK debug:** **Build → Build Bundle(s)/APK(s) → Build APK(s)**
  Hasil: `app/build/outputs/apk/debug/app-debug.apk`
- **APK release:** **Build → Generate Signed Bundle / APK → APK**
  Perlu keystore milik sekolah (lihat bagian berikutnya).

### Menggunakan command line

```bat
cd C:\laragon\www\exambrowser
gradlew.bat assembleDebug
```

Hasil: `app\build\outputs\apk\debug\app-debug.apk`

Untuk release tanpa Android Studio:

```bat
gradlew.bat assembleRelease
```

Perhatian: `assembleRelease` tanpa konfigurasi signing menghasilkan APK yang **belum ditandatangani** dan tidak bisa dipasang langsung. Ikuti langkah di bawah.

### Konfigurasi signing release

Belum disertakan karena kunci privat sekolah belum tersedia. Cara aman:

1. Buat keystore sekali saja, lalu simpan di tempat aman di luar folder proyek yang dibagikan:
   ```bat
   keytool -genkeypair -v -keystore C:\laragon\secure\exambrowser.jks -alias exambrowser -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Tambahkan ke `app/build.gradle.kts` (ganti nilai password dengan milik sekolah):
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("C:/laragon/secure/exambrowser.jks")
               storePassword = "..."
               keyAlias = "exambrowser"
               keyPassword = "..."
           }
       }
       buildTypes {
           getByName("release") {
               signingConfig = signingConfigs.getByName("release")
           }
       }
   }
   ```
3. Lebih baik lagi, simpan password di `local.properties` atau variabel lingkungan, **jangan** di-commit ke ZIP bersama collaborators.

> Jangan pernah membagikan file `.jks` bersama source code.

## 7. Cara memasang APK di HP siswa

1. Salin `app-debug.apk` (atau APK release yang sudah ditandatangani) ke HP siswa via kabel USB, Bluetooth, atau kartu memori.
2. Di HP siswa buka file APK tersebut.
3. Jika muncul peringatan *Install unknown apps*, izinkan hanya untuk aplikasi File Manager yang digunakan, lalu lanjutkan.
4. Setelah terpasang, hubungkan HP ke **Wi-Fi sekolah** sebelum membuka aplikasi.
5. Saat aplikasi dibuka, masukkan alamat CBT yang diberikan guru lalu tekan **HUBUNGKAN**.

## 8. Cara menggunakan aplikasi saat ujian

1. Siswa menghubungkan HP ke Wi-Fi sekolah.
2. Membuka **ExamBrowser SMKN 6 Muaro Jambi**.
3. Kolom alamat sudah berisi alamat terakhir jika pernah dipakai; jika belum, ketik alamat dari guru.
4. Tekan **HUBUNGKAN**.
5. Aplikasi memvalidasi alamat, memeriksa Wi-Fi, lalu menguji server.
   - Jika gagal, muncul pesan persis:
     **"Alamat CBT tidak dapat diakses. Periksa IP server dan pastikan HP terhubung ke Wi-Fi sekolah."**
     Sediakan tombol **Coba Lagi** dan **Ubah Alamat**.
   - Jika berhasil, halaman input hilang dan WebView CBT tampil penuh layar.
6. Siswa login ke CBT seperti biasa.
7. Siswa mengerjakan ujian, berpindah soal, dan mengirim jawaban **melalui CBT**.
8. Tombol Back di perangkat akan kembali ke halaman CBT sebelumnya. Jika sudah di halaman awal CBT, muncul konfirmasi keluar.
9. **Ganti Server** (dengan konfirmasi) kembali ke halaman awal untuk memasukkan alamat lain. Ujian yang sedang berjalan tidak diubah oleh ExamBrowser.
10. Setelah jawaban terkirim, siswa menutup aplikasi.

Indikator: abu = belum terhubung, kuning = sedang memeriksa, hijau = server siap/terhubung, merah = server tidak terjangkau. Indikator tersedia di halaman Input dan di header WebView.

## 9. Keterbatasan perlindungan screenshot

`FLAG_SECURE` hanya melindungi **jendela aplikasi ini**:

- mencegah screenshot standar Android;
- mencegah tampilan aplikasi ikut terekam pada screen recording standar;
- mencegah tampilan muncul di thumbnail *recent apps*.

Keterbatasan yang harus dipahami:

- **Tidak** menjamin pencegahan semua metode perekaman eksternal (misalnya kamera fisik ke layar, perangkat yang sudah di-*root*, alat perekam pada PC, atau aplikasi sistem khusus).
- **Tidak** memblokir screenshot yang diambil oleh sistem sebelum flag dipasang atau oleh komponen sistem tertentu.
- Aplikasi ini **tidak menyatakan** perlindungan absolut. Pengawasan tetap diperlukan.

## 10. Keterbatasan pembatasan perpindahan aplikasi

- Aplikasi menggunakan *immersive fullscreen* dan mendeteksi saat aplikasi masuk background atau kehilangan fokus.
- Saat kembali, muncul peringatan dan jumlah kejadian dicatat **di memori selama sesi aplikasi**. Ini **bukan** bukti otomatis kecurangan dan tidak dikirim ke server.
- Di **HP pribadi siswa**, aplikasi **tidak memaksa** masuk *Lock Task Mode* / kiosk. Hal itu hanya boleh dilakukan melalui proses pengelolaan perangkat yang sah (MDM/Device Owner), bukan dengan memaksa HP pribadi.
- Pembatasan penuh aplikasi lain hanya mungkin pada perangkat yang memang dikonfigurasi sebagai **perangkat khusus/terkelola**.

## 11. Panduan singkat iPhone (Safari + Guided Access)

Tidak dibuat aplikasi iOS. Untuk iPhone, gunakan **Safari** dan **Guided Access**.

1. Buka **Settings → Wi-Fi**, hubungkan ke Wi-Fi sekolah.
2. Buka Safari, ketik **alamat CBT yang diberikan guru** (contoh `http://192.168.10.1/cbt`), login seperti biasa.
3. Aktifkan Guided Access:
   - **Settings → Accessibility → Guided Access → ON**
   - Pilih **Passcode Settings → Set Guided Access Passcode** (berikan kode ke pengawas).
4. Kembali ke Safari, buka CBT, lalu tekan tombol **Home** tiga kali (iPhone dengan Home Button) atau tekan **Side Button** tiga kali (iPhone tanpa Home Button).
5. Di layar Guided Access, ketuk **Options** → matikan **Safari** pada bagian *Enable Keyboard*/navigasi bila perlu, lalu ketuk **Start**.
6. Untuk mengakhiri, tekan tombol Home/Side tiga kali, masukkan passcode, ketuk **End**.

> **PWA di iOS tidak dapat memblokir screenshot dan tidak dapat mengunci perangkat seperti kiosk.** Guided Access adalah fitur pengawasan bawaan iOS, bukan fitur yang dibuat oleh aplikasi ini.

## 12. Status pengujian

**Yang sudah diverifikasi di komputer build:**

- Gradle sync dan kompilasi `assembleDebug` berhasil setelah refactor input alamat manual dan penyesuaian `network_security_config.xml` (APK debug = 843.389 byte).
- Kompilasi `assembleRelease` dengan R8/resource shrinking berhasil setelah refactor, menghasilkan APK unsigned = 39.129 byte. APK ini **belum bisa dipasang** sampai ditandatangani dengan keystore sekolah.
- Manifest APK debug final diperiksa dengan `aapt dump badging`: package `id.sch.smkn6muarojambi.exambrowser`, `minSdk 21`, `targetSdk 35`, hanya permission Wi-Fi/Jaringan, launcher activity benar.
- Pemeriksaan kode: tidak ada `addJavascriptInterface`; `FLAG_SECURE` dipasang sebelum `setContentView`; pembatasan URL ada di `CbtSession.kt`; validasi alamat ada di `CbtAddress.kt`.

**Yang belum dilakukan:**

- Pengujian pada perangkat Android nyata (berbagai versi dan ukuran layar).
- Uji koneksi ke CBT sekolah — server contoh `192.168.10.1` tidak terjangkau dari komputer build, sehingga uji kompatibilitas login, navigasi soal, kirim jawaban, dan timer **belum bisa dilakukan di sini**.
- Uji screenshot/screen recording, perpindahan aplikasi, tombol Back, dan putus/sambung Wi-Fi.
- Uji lapangan untuk alamat HTTP/HTTPS, hostname lokal, port kustom, dan tombol **Ganti Server** di HP siswa.

Rincian dan kolom status ada di `UJI_COBA.md`. Tidak ada klaim bahwa pengujian perangkat telah lulus.

---

Dibuat oleh **Riko Hardona** — SMKN 6 Muaro Jambi.
