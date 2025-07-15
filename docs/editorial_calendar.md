# Kalender Editorial & Ide

Halaman kalender editorial berfungsi sebagai pusat perencanaan konten.

## Konsep

Modul ini menampilkan kalender harian, mingguan, atau bulanan yang memuat:

- **Ide Topik**: catatan singkat atau link riset.
- **Penjadwalan**: tanggal dan waktu publikasi.
- **Penugasan**: reporter/editor yang bertanggung jawab.
- **Status**: label proses seperti _draft_, _review_, hingga _publish_.

## Fungsi Utama

1. **Perencanaan & Monitoring** – melihat alur produksi dalam satu tampilan kalender.
2. **Kolaborasi & Transparansi** – semua anggota tim dapat memantau tugas dan tenggat.
3. **Manajemen Beban Kerja** – membantu distribusi penugasan agar merata.
4. **Sinkronisasi Deadline** – pengingat otomatis mendekati tenggat waktu.
5. **Integrasi** – menghubungkan hasil riset tren atau sistem manajemen proyek lain.

## Maksud & Tujuan

- Menjamin konsistensi jadwal publikasi.
- Mengurangi miskomunikasi dalam koordinasi tim.
- Mengoptimalkan kapasitas sumber daya.
- Mempermudah penyesuaian agenda jika ada berita mendadak.

## Alur Kerja Singkat

1. **Ide & Penjadwalan** – editor menambahkan topik ke kalender dan menetapkan reporter.
2. **Penulisan & Riset** – reporter mengerjakan draf dengan dukungan AI untuk riset cepat.
3. **Review & Revisi** – editor menilai dan memberi masukan hingga siap terbit.
4. **Persiapan Multimedia** – unggah aset dan lampirkan ke artikel.
5. **Persetujuan & Publikasi** – konten diterbitkan ke CMS dan kanal sosial.
6. **Analisis & Optimasi** – memantau performa serta rekomendasi topik lanjutan.
## Desain Halaman

Tampilan utama terdiri dari kalender bulanan dengan tombol tambah untuk memasukkan ide baru. Di bawah kalender terdapat daftar ringkas konten terjadwal. Pengguna dapat berpindah ke tampilan mingguan atau harian untuk detail lebih spesifik.

## Kolom Input

Form input mencakup beberapa kolom berikut:

| Kolom | Keterangan |
| --- | --- |
| Judul Topik | Nama atau tema artikel |
| Deskripsi | Catatan singkat, link referensi, atau angle yang ingin diambil |
| Tanggal & Waktu Publikasi | Memakai pemilih tanggal dan jam |
| Penanggung Jawab | Nama reporter/editor yang ditugaskan |
| Status | Dropdown: ide, dalam penulisan, review, siap publish |
| Lampiran | Opsional, mengunggah gambar atau dokumen pendukung |

## Struktur Penyimpanan Internal

Data tersimpan secara lokal menggunakan database Room. Tabel `editorial_entries` berisi kolom:

- `id` (Primary Key)
- `title`
- `description`
- `publishAt` (Unix timestamp)
- `assignee`
- `status`
- `attachmentPath` (jika ada)

## Tampilan Daftar & Preview

Setelah disimpan, entri muncul pada daftar di bawah kalender. Setiap item menampilkan judul, tanggal terbit, dan status. Mengetuk item membuka layar pratinjau yang berisi seluruh detail beserta tautan lampiran.

Tampilan pratinjau juga menyediakan opsi untuk mengedit atau mengubah status artikel sehingga alur kerja tetap konsisten dengan konsep dan maksud penggunaan kalender editorial.
