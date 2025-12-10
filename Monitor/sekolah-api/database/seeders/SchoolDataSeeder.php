<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Guru;
use App\Models\Kelas;
use App\Models\Jadwal;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class SchoolDataSeeder extends Seeder
{
    public function run()
    {
        // Create sample users
        User::create([
            'nama' => 'Admin User',
            'email' => 'admin@admin.com',
            'password' => Hash::make('admin123'),
            'role' => 'admin'
        ]);

        User::create([
            'nama' => 'Regular User',
            'email' => 'user@example.com',
            'password' => Hash::make('password'),
            'role' => 'user'
        ]);

        // Create sample teachers
        $guru1 = Guru::create([
            'nip' => '198501012010011001',
            'nama' => 'Budi Santoso',
            'jenis_kelamin' => 'Laki-laki',
            'email' => 'budi.santoso@school.com',
            'telepon' => '081234567890',
            'alamat' => 'Jl. Merdeka No. 123, Jakarta',
            'mata_pelajaran' => 'Matematika'
        ]);

        $guru2 = Guru::create([
            'nip' => '198702152011012002',
            'nama' => 'Siti Nurhaliza',
            'jenis_kelamin' => 'Perempuan',
            'email' => 'siti.nurhaliza@school.com',
            'telepon' => '081234567891',
            'alamat' => 'Jl. Sudirman No. 456, Jakarta',
            'mata_pelajaran' => 'Bahasa Indonesia'
        ]);

        $guru3 = Guru::create([
            'nip' => '198603202012011003',
            'nama' => 'Ahmad Hidayat',
            'jenis_kelamin' => 'Laki-laki',
            'email' => 'ahmad.hidayat@school.com',
            'telepon' => '081234567892',
            'alamat' => 'Jl. Thamrin No. 789, Jakarta',
            'mata_pelajaran' => 'Fisika'
        ]);

        $guru4 = Guru::create([
            'nip' => '198804102013012004',
            'nama' => 'Dewi Lestari',
            'jenis_kelamin' => 'Perempuan',
            'email' => 'dewi.lestari@school.com',
            'telepon' => '081234567893',
            'alamat' => 'Jl. Gatot Subroto No. 321, Jakarta',
            'mata_pelajaran' => 'Kimia'
        ]);

        $guru5 = Guru::create([
            'nip' => '198905252014011005',
            'nama' => 'Hendra Wijaya',
            'jenis_kelamin' => 'Laki-laki',
            'email' => 'hendra.wijaya@school.com',
            'telepon' => '081234567894',
            'alamat' => 'Jl. Kuningan No. 654, Jakarta',
            'mata_pelajaran' => 'Bahasa Inggris'
        ]);

        // Create sample classes
        $kelas1 = Kelas::create([
            'nama_kelas' => 'X RPL 1',
            'tingkat' => 10,
            'jurusan' => 'RPL',
            'wali_kelas_id' => $guru1->id,
            'ruangan' => 'Room 101',
            'kapasitas' => 32
        ]);

        $kelas2 = Kelas::create([
            'nama_kelas' => 'X RPL 2',
            'tingkat' => 10,
            'jurusan' => 'RPL',
            'wali_kelas_id' => $guru2->id,
            'ruangan' => 'Room 102',
            'kapasitas' => 30
        ]);

        $kelas3 = Kelas::create([
            'nama_kelas' => 'XI IPA 1',
            'tingkat' => 11,
            'jurusan' => 'IPA',
            'wali_kelas_id' => $guru3->id,
            'ruangan' => 'Room 201',
            'kapasitas' => 35
        ]);

        $kelas4 = Kelas::create([
            'nama_kelas' => 'XI IPS 1',
            'tingkat' => 11,
            'jurusan' => 'IPS',
            'wali_kelas_id' => $guru4->id,
            'ruangan' => 'Room 202',
            'kapasitas' => 33
        ]);

        // Create sample schedules
        Jadwal::create([
            'kelas_id' => $kelas1->id,
            'guru_id' => $guru1->id,
            'mata_pelajaran' => 'Matematika',
            'hari' => 'Senin',
            'jam_mulai' => '07:00:00',
            'jam_selesai' => '08:30:00',
            'ruangan' => 'Room 101'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas1->id,
            'guru_id' => $guru2->id,
            'mata_pelajaran' => 'Bahasa Indonesia',
            'hari' => 'Senin',
            'jam_mulai' => '08:30:00',
            'jam_selesai' => '10:00:00',
            'ruangan' => 'Room 101'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas2->id,
            'guru_id' => $guru1->id,
            'mata_pelajaran' => 'Matematika',
            'hari' => 'Selasa',
            'jam_mulai' => '07:00:00',
            'jam_selesai' => '08:30:00',
            'ruangan' => 'Room 102'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas3->id,
            'guru_id' => $guru3->id,
            'mata_pelajaran' => 'Fisika',
            'hari' => 'Rabu',
            'jam_mulai' => '07:00:00',
            'jam_selesai' => '08:30:00',
            'ruangan' => 'Lab Fisika'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas3->id,
            'guru_id' => $guru4->id,
            'mata_pelajaran' => 'Kimia',
            'hari' => 'Rabu',
            'jam_mulai' => '08:30:00',
            'jam_selesai' => '10:00:00',
            'ruangan' => 'Lab Kimia'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas4->id,
            'guru_id' => $guru5->id,
            'mata_pelajaran' => 'Bahasa Inggris',
            'hari' => 'Kamis',
            'jam_mulai' => '07:00:00',
            'jam_selesai' => '08:30:00',
            'ruangan' => 'Room 202'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas1->id,
            'guru_id' => $guru5->id,
            'mata_pelajaran' => 'Bahasa Inggris',
            'hari' => 'Jumat',
            'jam_mulai' => '07:00:00',
            'jam_selesai' => '08:30:00',
            'ruangan' => 'Room 101'
        ]);

        Jadwal::create([
            'kelas_id' => $kelas2->id,
            'guru_id' => $guru3->id,
            'mata_pelajaran' => 'Fisika',
            'hari' => 'Jumat',
            'jam_mulai' => '08:30:00',
            'jam_selesai' => '10:00:00',
            'ruangan' => 'Lab Fisika'
        ]);

        $this->command->info('Sample data seeded successfully!');
    }
}
