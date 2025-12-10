<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class SubstituteConfirmation extends Model
{
    use HasFactory;

    protected $table = 'substitute_confirmations';

    protected $fillable = [
        'guru_pengganti_id',
        'user_id',
        'confirmed_at',
        'keterangan',
    ];

    protected $casts = [
        'confirmed_at' => 'datetime',
    ];

    public function guruPengganti()
    {
        return $this->belongsTo(GuruPengganti::class, 'guru_pengganti_id');
    }

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }
}
