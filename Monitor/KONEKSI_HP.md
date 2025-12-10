# 📱 Cara Koneksi HP ke Server

## ✅ Server Sudah Berjalan!

**Server Laravel sedang running di:**

- URL: `http://192.168.40.3:8000`
- Status: ✅ ONLINE

## 📋 Langkah-Langkah Testing di HP:

### 1. Pastikan HP dan Laptop di WiFi yang SAMA

- HP: Terhubung ke WiFi yang sama dengan laptop
- Laptop: Terhubung ke WiFi dengan IP `192.168.40.3`

### 2. Cek Koneksi dari HP

Buka browser di HP dan akses:

```
http://192.168.40.3:8000
```

Jika muncul halaman Laravel, koneksi BERHASIL! ✅

### 3. Test API Endpoint

Coba akses endpoint API:

```
http://192.168.40.3:8000/api/
```

### 4. Build & Install APK

- Build APK di Android Studio
- Install APK ke HP
- Buka aplikasi dan login

## 🔐 Akun Testing:

| Role               | Email               | Password |
| ------------------ | ------------------- | -------- |
| **Admin**          | admin@smkn2.sch.id  | password |
| **Siswa**          | siswa@smkn2.sch.id  | password |
| **Kurikulum**      | guru@smkn2.sch.id   | password |
| **Kepala Sekolah** | kepsek@smkn2.sch.id | password |

## 🔧 Troubleshooting:

### Jika tidak bisa koneksi:

1. **Pastikan Firewall tidak memblokir**

   ```powershell
   # Jalankan di PowerShell (Admin):
   New-NetFirewallRule -DisplayName "Laravel Server" -Direction Inbound -LocalPort 8000 -Protocol TCP -Action Allow
   ```

2. **Cek IP masih sama**
   ```powershell
   ipconfig
   ```
3. **Restart Server Laravel**

   - Tekan Ctrl+C di terminal
   - Jalankan ulang: `php artisan serve --host=0.0.0.0 --port=8000`

4. **Pastikan XAMPP MySQL berjalan**
   - Buka XAMPP Control Panel
   - Start MySQL (jika belum)

## 📝 Konfigurasi yang Sudah Diset:

**File: RetrofitClient.kt**

```kotlin
private const val BASE_URL = "http://192.168.40.3:8000/api/"
```

**Server Laravel:**

```bash
php artisan serve --host=0.0.0.0 --port=8000
```

## ⚠️ PENTING!

- **Jangan matikan terminal server** saat testing
- **HP dan Laptop harus di WiFi yang sama**
- **IP 192.168.40.3** adalah IP komputer Anda saat ini
- Jika IP berubah (pindah WiFi), update lagi di `RetrofitClient.kt`

## 🚀 Siap Testing!

Aplikasi sudah siap ditest di HP asli dengan auto-detect role login! 🎉
