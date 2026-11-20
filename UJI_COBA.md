# Daftar Pengujian ExamBrowser

Status berikut sengaja jujur: **belum diuji pada perangkat nyata** oleh pembuat proyek. Tandai centang hanya setelah benar-benar dilakukan.

## A. Pengujian statis / build

| No | Pengujian | Status | Catatan |
| --- | --- | --- | --- |
| 1 | Gradle sync tanpa error | LULUS | Gradle 8.7 + AGP 8.5.2 + Kotlin 1.9.24; SDK Platform 35 & Build-Tools 35.0.0. Muncul peringatan AGP 8.5.2 hanya teruji sampai compileSdk 34, tetapi build tetap berhasil. |
| 2 | Kompilasi `assembleDebug` setelah refactor input manual | LULUS | `app-debug.apk` = 843.389 byte. Package `id.sch.smkn6muarojambi.exambrowser`, minSdk 21, targetSdk 35 (diverifikasi sebelumnya dengan `aapt dump badging`). |
| 3 | Kompilasi `assembleRelease` setelah refactor | LULUS (kompilasi) | R8 + resource shrinking berhasil; `app-release-unsigned.apk` = 39.129 byte. Tidak dapat dipasang karena belum ditandatangani — sesuai kondisi "signing belum tersedia". |
| 4 | Tidak ada `addJavascriptInterface` | SUDAH DICEK (kode) | Tidak ada bridge JavaScript. |
| 5 | `FLAG_SECURE` dipasang sebelum `setContentView` | SUDAH DICEK (kode) | `MainActivity.onCreate`. |
| 6 | Validasi alamat dengan `java.net.URI` | SUDAH DICEK (kode) | `CbtAddress.kt`: auto-prefix `http://`, hanya HTTP/HTTPS, tolak userinfo, host/port/path diparsing, tolak `..`. Tidak memakai `contains`. |
| 7 | Pembatasan navigasi berdasarkan origin | SUDAH DICEK (kode) | `CbtSession.kt`: skema + host + effective port harus sama dengan alamat tervalidasi; semua path di origin diizinkan; eksternal diblokir. |
| 8 | Penyimpanan alamat terakhir | SUDAH DICEK (kode) | `SharedPreferences` `exambrowser` → `cbt_address`; hanya alamat, bukan kredensial. |
| 9 | Cleartext HTTP untuk alamat manual | SUDAH DICEK (kode) | `network_security_config.xml` memakai `base-config cleartextTrafficPermitted="true"`; pembatasan tetap di WebView. |
| 10 | Manifest APK debug diverifikasi dengan `aapt dump badging` | LULUS | Diverifikasi pada APK final: package `id.sch.smkn6muarojambi.exambrowser`, minSdk 21, targetSdk 35, hanya 2 permission Wi-Fi/Jaringan, launcher activity benar. |

## B. Pengujian perangkat (belum dilakukan)

| No | Pengujian | Status | Cara singkat |
| --- | --- | --- | --- |
| 1 | Android versi berbeda (mis. 5.1, 8.1, 11, 13) | BELUM | Pasang APK, buka aplikasi |
| 2 | Ukuran layar berbeda (HP kecil & tablet) | BELUM | Cek layout halaman input, header, dan WebView |
| 3 | Wi-Fi lokal tanpa internet | BELUM | Matikan data seluler, sambung Wi-Fi sekolah |
| 4 | Input kosong & format salah | BELUM | Tekan HUBUNGKAN dengan kolom kosong, lalu dengan `ftp://...` dan `user:pass@...` |
| 5 | Auto-prefix `http://` | BELUM | Masukkan `192.168.10.1/cbt`, pastikan terbuka |
| 6 | IP lokal + hostname lokal + HTTPS + port kustom | BELUM | Coba `http://192.168.10.1/cbt`, `http://cbt.sekolah.local/cbt`, `https://...`, `http://host:8080/cbt` |
| 7 | Server CBT aktif | BELUM | CBT harus terbuka setelah HUBUNGKAN |
| 8 | Server CBT tidak aktif | BELUM | Muncul pesan **"Alamat CBT tidak dapat diakses. Periksa IP server dan pastikan HP terhubung ke Wi-Fi sekolah."** + tombol Coba Lagi & Ubah Alamat |
| 9 | Alamat tersimpan & prefilled | BELUM | Tutup aplikasi, buka lagi, cek kolom terisi alamat terakhir |
| 10 | Ganti Server dengan konfirmasi | BELUM | Saat di CBT, tekan Ganti Server, Batal lalu Ganti, cek kembali ke halaman input |
| 11 | Login CBT | BELUM | Login dengan akun uji |
| 12 | Navigasi soal | BELUM | Berpindah soal beberapa kali |
| 13 | Gambar/file/JS di origin CBT | BELUM | Pastikan aset di path lain pada server yang sama tetap tampil |
| 14 | Pengiriman jawaban | BELUM | Kirim jawaban uji, cek di CBT |
| 15 | Timer ujian | BELUM | Pastikan timer berjalan normal |
| 16 | Blokir situs eksternal | BELUM | Coba tautan eksternal dari CBT, harus diblokir |
| 17 | Screenshot standar | BELUM | Tekan kombinasi tombol screenshot |
| 18 | Screen recording standar | BELUM | Rekam layar dengan fitur sistem |
| 19 | Recent apps thumbnail | BELUM | Cek thumbnail tidak menampilkan isi ujian |
| 20 | Pindah aplikasi lalu kembali | BELUM | Harus muncul peringatan |
| 21 | Tombol Back | BELUM | Kembali internal CBT, konfirmasi keluar di awal |
| 22 | Koneksi putus & tersambung lagi | BELUM | Putus Wi-Fi, lalu sambung, tekan Coba Lagi |

## C. Cara melaporkan hasil

Isi kolom **Status** dengan `LULUS` / `GAGAL` dan tambahkan kolom keterangan: versi Android, merek HP, alamat CBT yang dipakai, dan langkah yang dilakukan. Jangan menulis `LULUS` sebelum pengujian benar-benar dijalankan.

> Server CBT `192.168.10.1` tidak terjangkau dari komputer build, sehingga uji kompatibilitas login, soal, gambar, file, timer, dan pengiriman jawaban **belum bisa dilakukan di sini**. Tidak ada klaim bahwa pengujian perangkat telah lulus.
