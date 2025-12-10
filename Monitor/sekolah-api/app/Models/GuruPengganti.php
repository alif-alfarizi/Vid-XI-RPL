<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class GuruPengganti extends Model
{
    use HasFactory;

    protected $table = 'guru_pengganti';

    protected $fillable = [
        'tanggal',
        'jadwal_id',
        'guru_asli_id',
        'guru_pengganti_id',
        'alasan',
        'keterangan',
        'status',
        'disetujui_oleh',
        'disetujui_pada',
    ];

    protected $casts = [
        'tanggal' => 'date',
        'disetujui_pada' => 'datetime',
    ];

    // Relasi ke Jadwal
    public function jadwal()
    {
        return $this->belongsTo(Jadwal::class, 'jadwal_id');
    }

    // Relasi ke Guru Asli
    public function guruAsli()
    {
        return $this->belongsTo(Guru::class, 'guru_asli_id');
    }

    // Relasi ke Guru Pengganti
    public function guruPengganti()
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id');
    }

    // Relasi ke User yang menyetujui
    public function disetujuiOleh()
    {
        return $this->belongsTo(User::class, 'disetujui_oleh');
    }

    // Relasi ke konfirmasi siswa bahwa guru pengganti sudah masuk
    public function confirmations()
    {
        return $this->hasMany(\App\Models\SubstituteConfirmation::class, 'guru_pengganti_id');
    }

    // Scope untuk hari ini
    public function scopeHariIni($query)
    {
        return $query->whereDate('tanggal', today());
    }

    // Scope untuk status pending
    public function scopePending($query)
    {
        return $query->where('status', 'Pending');
    }

    // Scope untuk status disetujui
    public function scopeDisetujui($query)
    {
        return $query->where('status', 'Disetujui');
    }

    // Helper untuk badge warna status
    public function getStatusColorAttribute()
    {
        return match ($this->status) {
            'Pending' => 'warning',
            'Disetujui' => 'success',
            'Ditolak' => 'danger',
            'Selesai' => 'secondary',
            default => 'primary',
        };
    }

    // Helper untuk badge warna alasan
    public function getAlasanColorAttribute()
    {
        return match ($this->alasan) {
            'Sakit' => 'danger',
            'Izin' => 'warning',
            'Cuti' => 'info',
            'Dinas Luar' => 'primary',
            'Lainnya' => 'secondary',
            default => 'secondary',
        };
    }
}
