# Desain Halaman & Relasi Antar Fitur

Dokumen ini memetakan rancangan halaman pada aplikasi Penmas News beserta alur perpindahannya. Setiap modul utama memiliki tampilan tersendiri yang saling terhubung melalui menu beranda.

## 1. Beranda
- Tampilan awal berisi daftar tombol menuju setiap fitur.
- Menjadi hub utama untuk berpindah ke **Kalender Editorial**, **Penulisan Kolaboratif**, **Asistensi AI**, **Manajemen Aset**, **Workflow Approval**, dan **Analitik**.

## 2. Kalender Editorial & Ide
- Menampilkan daftar atau kalender berisi ide topik, penjadwalan, penugasan, dan status.
- Dari halaman ini pengguna dapat membuka dokumen di **Penulisan Kolaboratif** atau menetapkan proses ke **Workflow Approval**.

## 3. Penulisan Kolaboratif
- Editor teks bersama dengan dukungan komentar, histori, dan _track changes_.
- Dapat diakses dari **Kalender Editorial** maupun langsung dari **Beranda**.
- Setelah selesai, draf dikirim ke **Workflow Approval**.

## 4. Asistensi AI
- Fitur opsional yang membantu peringkasan, saran judul, dan pengecekan bahasa.
- Dipanggil dari dalam **Penulisan Kolaboratif** atau dari **Beranda** untuk kebutuhan terpisah.
- Halaman ini menyediakan kolom masukan teks beserta sejumlah isian khusus seperti Dasar, Tersangka, TKP dan Waktu Kejadian, hingga Pasal yang dipersangkakan.

## 5. Manajemen Aset Multimedia
- Halaman pengunggahan serta katalog gambar, video, dan dokumen.
- File yang dipilih bisa disisipkan ke artikel pada **Penulisan Kolaboratif**.

## 6. Workflow Approval
- Memuat daftar draf dengan status _draft_, _review_, hingga _final_.
- Setiap entri dapat dibuka kembali di **Penulisan Kolaboratif** jika perlu revisi.
- Setelah disetujui, konten dipublikasikan ke Blogspot dari **Kalender Editorial**.
- Tampilan daftar menggunakan bingkai list yang konsisten dengan modul lain.

## 7. Analitik & Umpan Balik
- Dashboard metrik performa artikel dan umpan balik pembaca.
- Rekomendasi topik baru dapat langsung ditambahkan ke **Kalender Editorial**.
- Lihat rincian desain pada [dokumen khusus](analytics_dashboard.md).

### Alur Utama Pengguna
1. Pengguna membuka **Beranda**.
2. Dari **Kalender Editorial**, pilih topik atau buat baru.
3. Masuk ke **Penulisan Kolaboratif** untuk mengerjakan draf.
4. Gunakan **Asistensi AI** atau **Manajemen Aset** jika diperlukan.
5. Kirim draf ke **Workflow Approval**. Setelah disetujui, gunakan tombol publikasi di **Kalender Editorial**.
6. Setelah dipublikasikan, pantau hasilnya di **Analitik** dan kembali merencanakan topik selanjutnya pada **Kalender Editorial**.
