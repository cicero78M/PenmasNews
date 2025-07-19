# Manajemen Login WordPress

Dokumen ini menjelaskan cara kerja sesi login WordPress pada aplikasi **Penmas News** dan bagaimana sesi dibuat ulang secara otomatis.

## Tujuan
- Mempermudah proses publikasi dari kalender editorial.
- Menghindari pengisian ulang kredensial setiap saat.

## Alur
1. Pengguna memasukkan **Base URL**, **Username**, dan **Application Password** pada halaman login WordPress.
2. Aplikasi menyimpan data tersebut di `CMSPrefs`.
3. Saat akan mempublikasikan ke WordPress, `CMSIntegration` memanggil `WordpressAuth.loginBlocking()` jika token belum ada.
4. Token JWT yang diterima disimpan dan dipakai pada header `Authorization` tiap publikasi berikutnya.

Dengan mekanisme ini workflow publikasi berjalan tanpa perlu login berulang kali.
