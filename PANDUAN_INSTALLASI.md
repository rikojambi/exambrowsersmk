# Panduan Instalasi ExamBrowser SMKN 6 Muaro Jambi

## A. Untuk siswa (memasang APK)

**Yang disiapkan:** file `app-debug.apk` (untuk uji coba) atau APK release yang sudah ditandatangani sekolah.

1. Hubungkan HP ke **Wi-Fi sekolah** (tidak perlu internet).
2. Salin APK ke HP melalui kabel USB, Bluetooth, atau kartu memori.
3. Buka file APK dari aplikasi File Manager.
4. Jika Android menampilkan *Install unknown apps*, berikan izin hanya untuk File Manager tersebut, lalu tekan **Install**.
5. Setelah selesai, buka aplikasi **ExamBrowser SMKN 6 Muaro Jambi**.
6. Pada halaman awal, masukkan alamat CBT yang diberikan guru (contoh `http://192.168.10.1/cbt`) lalu tekan **HUBUNGKAN**.

> Android 5.0 (API 21) atau lebih baru diperlukan. Jika tombol Install tidak muncul, cek pengaturan *Unknown sources* / *Install unknown apps*.

## B. Untuk pengembang (menyiapkan lingkungan build)

1. Pasang Android Studio versi Ladybug (2024.2.1) atau lebih baru (JDK 17 sudah termasuk).
2. Pasang Android SDK Platform **35** dan Build-Tools **35.x** lewat SDK Manager.
3. Buka folder proyek `C:\laragon\www\exambrowser` melalui **File → Open**.
4. Tunggu *Gradle sync*. Koneksi internet diperlukan pada sinkronisasi pertama.
5. Hubungkan HP dengan USB Debugging aktif, atau gunakan emulator, lalu tekan **Run**.

## C. Tentang alamat server

Alamat CBT **tidak ditanam permanen** di aplikasi. Siswa memasukkan alamat tersebut di halaman awal. Tidak perlu mengubah `Config.kt` (file ini sudah tidak ada) atau hardcode IP sebelum membagikan APK.

Aturan alamat:

- IP lokal dan hostname lokal diterima;
- `http://` dan `https://` diterima;
- jika tanpa skema, `http://` ditambahkan otomatis;
- username/password pada URL ditolak;
- port boleh eksplisit, misalnya `http://cbt.sekolah.local:8080/cbt`.

`network_security_config.xml` sudah mengizinkan HTTP secara umum karena alamat ditentukan siswa. Pembatasan navigasi dilakukan di `CbtSession.kt` dengan membandingkan origin (skema + host + port) terhadap alamat yang divalidasi.

Alamat terakhir disimpan lokal di `SharedPreferences` (`exambrowser` → `cbt_address`). Username/password CBT **tidak pernah disimpan**.

## D. Zipping proyek

File `Exambrowser_SMKN6MJ.zip` berisi seluruh source code dan dokumentasi. `local.properties` sengaja tidak disertakan karena berisi path SDK komputer lokal. APK debug disertakan terpisah di folder `APK/app-debug.apk`.
