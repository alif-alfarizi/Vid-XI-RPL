<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Kelas;
use Illuminate\Support\Facades\Hash;

class SiswaSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        // Pastikan ada kelas
        $kelas = Kelas::first();

        if (!$kelas) {
            $kelas = Kelas::create([
                'nama_kelas' => 'XI RPL 1'
            ]);
        }

        // Buat siswa test
        $siswaData = [
            [
                'nama' => 'Ahmad Siswa',
                'email' => 'siswa@smkn2.sch.id',
                'password' => Hash::make('password'),
                'role' => 'siswa',
                'kelas_id' => $kelas->id
            ],
            [
                'nama' => 'Budi Pelajar',
                'email' => 'budi@smkn2.sch.id',
                'password' => Hash::make('password'),
                'role' => 'siswa',
                'kelas_id' => $kelas->id
            ],
            [
                'nama' => 'Citra Murid',
                'email' => 'citra@smkn2.sch.id',
                'password' => Hash::make('password'),
                'role' => 'siswa',
                'kelas_id' => $kelas->id
            ],
        ];

        foreach ($siswaData as $siswa) {
            User::updateOrCreate(
                ['email' => $siswa['email']],
                $siswa
            );
        }

        $this->command->info('Siswa berhasil ditambahkan!');
        $this->command->info('Login siswa: siswa@smkn2.sch.id / password');
    }
}
