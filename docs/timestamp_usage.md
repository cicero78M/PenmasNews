# Format Tanggal & Timestamp

Aplikasi menggunakan dua format tanggal agar konsisten antara input dan output:

* `dd/MM/yyyy` untuk kolom **event_date** pada backend dan isian tanggal di UI.
* `yyyy-MM-dd HH:mm:ss` untuk informasi `createdAt`, `lastUpdate`, dan pencatatan log.

Utility `DateUtils` menyediakan helper `DateUtils.now()` untuk mendapatkan waktu
saat ini dalam format tanggal dan waktu di atas, serta `DateUtils.formatTimestamp()`
untuk menampilkan nilai Unix timestamp yang disimpan pada `ChangeLogEntry`.

### Skenario Penggunaan Timestamp
1. Pengguna menambahkan agenda baru di kalender editorial.
2. Aplikasi memanggil `DateUtils.now()` untuk mengisi `createdAt` dan `lastUpdate`.
3. Setiap penyimpanan atau perubahan konten memanggil `System.currentTimeMillis() / 1000L`
   untuk membuat timestamp detik pada `ChangeLogEntry`.
4. Daftar log ditampilkan dengan `DateUtils.formatTimestamp()` sehingga seluruh
tanggal tersaji konsisten dalam zona waktu Asia/Jakarta.
