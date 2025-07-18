# PenmasNews

This repository contains a minimal Android application skeleton written in Kotlin.
It demonstrates the core structure for a news production workflow app. Several
features are represented as placeholder classes.

When the app starts you will be presented with a simple login screen. Use the
credentials `@papiqo` with password `12345` to log in as the `penulis` actor or
`@penmas` with the same password to log in as the `editor` actor.

To create a new account simply enter a username, password and choose a role on
the sign‑up form. No registration token is required.

## Building the App

This project uses Gradle. Because the Android SDK isn't bundled in this
repository, build and run it using Android Studio with the SDK installed.

```
# From the project root
./gradlew assembleDebug
```

Open the project in Android Studio to edit and run on a device or emulator.

Create a `.env` file in the project root to supply your API keys.  These
credentials are used when signing in with Google OAuth2 in order to publish
approved articles to Blogspot:

```
OPENAI_API_KEY=sk-...
BLOGGER_API_KEY=your-blogger-key
BLOGGER_BLOG_ID=your-blog-id
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
- [Panduan Format Tanggal](docs/timestamp_usage.md)
- [Halaman Analitik](docs/analytics_dashboard.md)
