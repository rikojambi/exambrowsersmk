# Aturan ProGuard untuk aplikasi ini.
# Tidak ada JavaScript bridge, jadi tidak perlu aturan @JavascriptInterface.

# Pastikan kelas Application dan Activity tidak dihapus (dibutuhkan Android).
-keep class id.sch.smkn6muarojambi.exambrowser.ExamApplication { *; }
-keep class id.sch.smkn6muarojambi.exambrowser.MainActivity { *; }
