# Panduan Penggunaan ExamBrowser (Siswa)

## Sebelum ujian

1. Hubungkan HP ke **Wi-Fi sekolah**.
2. Pastikan daya baterai cukup.
3. Tutup aplikasi lain yang tidak diperlukan.
4. Siapkan alamat CBT yang diberikan guru, misalnya `http://192.168.10.1/cbt`.
5. Buka **ExamBrowser SMKN 6 Muaro Jambi**.

## Halaman awal (input alamat)

Saat pertama dibuka, akan tampil:

- nama sekolah **SMKN 6 Muaro Jambi**;
- nama aplikasi **ExamBrowser SMKN 6 Muaro Jambi**;
- kolom alamat IP/URL CBT;
- tombol **HUBUNGKAN**;
- indikator status koneksi (abu = belum terhubung, kuning = memeriksa, hijau = siap, merah = gagal).

Jika aplikasi pernah dipakai, kolom alamat sudah terisi dengan alamat terakhir. Anda boleh mengubahnya langsung atau menekan **Ganti Server** saat berada di halaman CBT.

### Menulis alamat

- Boleh IP: `http://192.168.10.1/cbt`
- Boleh hostname lokal: `http://cbt.sekolah.local/cbt`
- Boleh HTTPS: `https://cbt.sekolah.local/cbt`
- Boleh port: `http://192.168.10.1:8080/cbt`
- Jika tidak menulis `http://`, aplikasi akan menambahkannya otomatis: `192.168.10.1/cbt` → `http://192.168.10.1/cbt`
- Jangan memasukkan `https://` jika server hanya mendukung HTTP, dan sebaliknya.
- Jangan mengisi alamat dengan username/password, misalnya `http://user:pass@192.168.10.1/cbt` akan ditolak.
- Format selain HTTP/HTTPS (misalnya `ftp://`, `file://`) ditolak.

## Saat menekan HUBUNGKAN

Aplikasi akan:

1. memeriksa apakah kolom alamat kosong atau tidak valid;
2. memeriksa apakah HP terhubung Wi-Fi;
3. menguji apakah server CBT dapat diakses;
4. membuka CBT di WebView jika berhasil.

Jika gagal, akan muncul pesan persis:

> **Alamat CBT tidak dapat diakses. Periksa IP server dan pastikan HP terhubung ke Wi-Fi sekolah.**

Tersedia dua tombol:

- **Coba Lagi** — ulangi pengecekan dengan alamat yang sama;
- **Ubah Alamat** — kembali mengedit kolom alamat.

## Saat mengerjakan ujian

- Login menggunakan akun CBT seperti biasa. Aplikasi **tidak menyimpan username/password**.
- Gunakan navigasi soal pada halaman CBT.
- Jawaban dikirim melalui tombol yang tersedia di CBT.
- Timer ujian tetap dikendalikan oleh CBT, bukan oleh aplikasi ini.
- Aplikasi hanya mengizinkan halaman di server CBT yang Anda masukkan. Situs lain diblokir.
- Semua halaman, gambar, file, dan skrip di server CBT yang sama tetap diizinkan agar CBT berfungsi normal.

## Tombol dan indikator

| Elemen | Fungsi |
| --- | --- |
| Titik hijau | Server CBT terjangkau / siap |
| Titik merah | Server CBT tidak terjangkau |
| Titik kuning | Sedang memeriksa koneksi |
| **HUBUNGKAN** | Validasi alamat, cek Wi-Fi, cek server, lalu buka CBT |
| **Coba Lagi** | Ulangi pengecekan saat gagal |
| **Ubah Alamat** | Fokus ke kolom alamat saat gagal |
| **Ganti Server** | Kembali ke halaman awal dengan konfirmasi |
| Ikon panah melingkar | Muat ulang halaman CBT |
| Tombol Back perangkat | Kembali ke halaman CBT sebelumnya; jika sudah di awal, muncul konfirmasi keluar |

## Jika aplikasi sempat tertutup/berganti aplikasi

Saat kembali ke ExamBrowser, akan muncul peringatan bahwa aplikasi sempat tidak aktif. Kejadian hanya dicatat di memori selama sesi dan **bukan** penilaian otomatis kecurangan. Peringatan ini bukan bukti bahwa perekaman terjadi; pengawasan tetap diperlukan.

## Selesai ujian

1. Pastikan status pengiriman jawaban dari CBT sudah berhasil.
2. Tekan Back pada halaman awal CBT dan pilih **Keluar**, atau tutup aplikasi dari *recent apps*.
3. Putuskan Wi-Fi hanya setelah ujian selesai jika diperlukan.

## Yang tidak boleh dilakukan

- Mencoba membuka situs lain dari dalam aplikasi (diblokir).
- Mencoba membuka aplikasi browser lain saat ujian berlangsung.
- Mengira aplikasi ini mencegah semua bentuk perekaman — tidak ada jaminan tersebut.
- Mengira tombol **Ganti Server** mengubah jawaban di CBT — tombol itu hanya mengubah alamat yang dibuka ExamBrowser.
