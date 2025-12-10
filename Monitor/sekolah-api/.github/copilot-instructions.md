# Laravel API - Aplikasi Monitoring Kelas

Project Laravel API untuk sistem monitoring kelas sekolah yang dapat terhubung dengan aplikasi Android.

## Struktur Project

- **Framework**: Laravel 9.x
- **Database**: MySQL (database: sekolah)
- **API Type**: RESTful API
- **Authentication**: Laravel Sanctum (ready untuk implementasi)

## Endpoints Utama

### User Management API
- `GET /api/users` - List semua users
- `POST /api/users` - Create user baru  
- `GET /api/users/{id}` - Detail user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Database Schema

### Tabel Users
- `id` (primary key)
- `nama` (string) - Nama lengkap user
- `email` (string, unique) - Email user
- `password` (string, hashed) - Password user
- `role` (string) - Role user (siswa/guru/admin)
- `timestamps` - created_at, updated_at

## Development Setup

1. Pastikan MySQL berjalan dan database 'sekolah' sudah dibuat
2. Setup environment variables di `.env`
3. Install dependencies: `composer install`
4. Run migrations: `php artisan migrate`
5. Start server: `php artisan serve`

## API Response Format

Semua response menggunakan format JSON standar:

```json
{
    "success": boolean,
    "message": "string",
    "data": object/array
}
```

## Android Integration

Base URL untuk Android: `http://127.0.0.1:8000/api/`

Content-Type: `application/json`
Accept: `application/json`

## Status

✅ Project setup completed
✅ Database migration completed  
✅ User API endpoints ready
✅ Documentation completed