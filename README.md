# PenmasNews

This repository contains a minimal Android application skeleton written in Kotlin.
It demonstrates the core structure for a news production workflow app. Several
features are represented as placeholder classes.

## Building the App

This project uses Gradle. Because the Android SDK isn't bundled in this
repository, build and run it using Android Studio with the SDK installed.

```
# From the project root
./gradlew assembleDebug
```

Open the project in Android Studio to edit and run on a device or emulator.

Create a `.env` file in the project root to supply your OpenAI API key:

```
OPENAI_API_KEY=sk-...
```
An example file `.env.example` is provided.

## Formulir Asistensi AI

Halaman ini kini berfokus pada pencatatan data penyidikan. Selain tanggal,
catatan, dan masukan teks, disediakan kolom untuk mengisi:

- **Dasar**
- **Tersangka**
- **TKP dan Waktu Kejadian**
- **Kronologi Penyelidikan dan Penyidikan**
- **Modus Operandi**
- **Barang Bukti**
- **Pasal yang dipersangkakan**
- **Ancaman Hukuman**

## Dokumentasi Desain

- [Kalender Editorial & Ide](docs/editorial_calendar.md)
- [Desain Halaman & Relasi Fitur](docs/ui_overview.md)
