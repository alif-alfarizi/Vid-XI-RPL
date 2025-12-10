<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Guru extends Model
{
    use HasFactory;

    protected $table = 'guru';

    protected $fillable = [
        'nip',
        'nama',
        'jenis_kelamin',
        'email',
        'telepon',
        'alamat',
        'mata_pelajaran',
        'foto',
    ];

    public function jadwal()
    {
        return $this->hasMany(Jadwal::class);
    }

    public function kelasWali()
    {
        return $this->hasMany(Kelas::class, 'wali_kelas_id');
    }
}
