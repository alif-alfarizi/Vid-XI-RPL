<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Kelas;
use App\Models\Guru;
use Illuminate\Support\Facades\Hash;

class DevelopmentSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        // Create default users
        User::create([
            'nama' => 'Admin User',
            'name' => 'Admin User',
            'email' => 'admin@smkn2.sch.id',
            'password' => Hash::make('password'),
            'role' => 'Admin',
        ]);

        User::create([
            'nama' => 'Siswa Test',
            'name' => 'Siswa Test',
            'email' => 'siswa@smkn2.sch.id',
            'password' => Hash::make('password'),
            'role' => 'Siswa',
        ]);

        User::create([
            'nama' => 'Kurikulum Test',
            'name' => 'Kurikulum Test',
            'email' => 'kurikulum@smkn2.sch.id',
            'password' => Hash::make('password'),
            'role' => 'Kurikulum',
        ]);

        User::create([
            'nama' => 'Kepala Sekolah',
            'name' => 'Kepala Sekolah',
            'email' => 'kepsek@smkn2.sch.id',
            'password' => Hash::make('password'),
            'role' => 'Kepala Sekolah',
        ]);

        // Create default kelas
        Kelas::create([
            'nama_kelas' => '10 RPL',
            'tingkat' => 10,
            'jurusan' => 'RPL',
        ]);

        Kelas::create([
            'nama_kelas' => '11 RPL',
            'tingkat' => 11,
            'jurusan' => 'RPL',
        ]);

        Kelas::create([
            'nama_kelas' => '12 RPL',
            'tingkat' => 12,
            'jurusan' => 'RPL',
        ]);

        // Create default guru
        Guru::create([
            'nama' => 'Siti Aminah',
            'nip' => '198501012010012001',
            'email' => 'siti@smkn2.sch.id',
            'mata_pelajaran' => 'Matematika',
        ]);

        Guru::create([
            'nama' => 'Budi Santoso',
            'nip' => '198601012011012001',
            'email' => 'budi@smkn2.sch.id',
            'mata_pelajaran' => 'Bahasa Indonesia',
        ]);

        Guru::create([
            'nama' => 'Adi Nugroho',
            'nip' => '198701012012012001',
            'email' => 'adi@smkn2.sch.id',
            'mata_pelajaran' => 'Pemrograman Web',
        ]);
    }
}
