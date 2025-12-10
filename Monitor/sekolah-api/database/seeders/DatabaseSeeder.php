<?php

namespace Database\Seeders;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Guru;
use App\Models\Kelas;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        // Create admin user
        User::updateOrCreate(
            ['email' => 'admin@smkn2.sch.id'],
            [
                'nama' => 'Administrator',
                'password' => Hash::make('password'),
                'role' => 'admin',
            ]
        );

        // Create other test users
        User::updateOrCreate(
            ['email' => 'siswa@smkn2.sch.id'],
            [
                'nama' => 'Siswa Test',
                'password' => Hash::make('password'),
                'role' => 'siswa',
            ]
        );

        User::updateOrCreate(
            ['email' => 'guru@smkn2.sch.id'],
            [
                'nama' => 'Guru Test',
                'password' => Hash::make('password'),
                'role' => 'kurikulum',
            ]
        );

        User::updateOrCreate(
            ['email' => 'kepsek@smkn2.sch.id'],
            [
                'nama' => 'Kepala Sekolah Test',
                'password' => Hash::make('password'),
                'role' => 'kepala-sekolah',
            ]
        );

        // Create default Guru
        $guru1 = Guru::updateOrCreate(
            ['nip' => '198501012010011001'],
            [
                'nama' => 'Budi Santoso, S.Pd',
                'jenis_kelamin' => 'Laki-laki',
                'email' => 'budi.santoso@smkn2.sch.id',
                'telepon' => '081234567890',
                'alamat' => 'Jl. Pendidikan No. 1',
                'mata_pelajaran' => 'Matematika',
            ]
        );

        $guru2 = Guru::updateOrCreate(
            ['nip' => '198602022011012002'],
            [
                'nama' => 'Siti Aminah, S.Pd',
                'jenis_kelamin' => 'Perempuan',
                'email' => 'siti.aminah@smkn2.sch.id',
                'telepon' => '081234567891',
                'alamat' => 'Jl. Guru No. 2',
                'mata_pelajaran' => 'Bahasa Indonesia',
            ]
        );

        $guru3 = Guru::updateOrCreate(
            ['nip' => '198703032012013003'],
            [
                'nama' => 'Ahmad Wijaya, S.Kom',
                'jenis_kelamin' => 'Laki-laki',
                'email' => 'ahmad.wijaya@smkn2.sch.id',
                'telepon' => '081234567892',
                'alamat' => 'Jl. Teknologi No. 3',
                'mata_pelajaran' => 'Pemrograman',
            ]
        );

        // Create default Kelas
        $kelas1 = Kelas::updateOrCreate(
            ['nama_kelas' => 'XI RPL 1'],
            [
                'tingkat' => '11',
                'jurusan' => 'Rekayasa Perangkat Lunak',
                'wali_kelas_id' => $guru1->id,
                'ruangan' => 'R-101',
                'kapasitas' => 36,
            ]
        );

        $kelas2 = Kelas::updateOrCreate(
            ['nama_kelas' => 'XI RPL 2'],
            [
                'tingkat' => '11',
                'jurusan' => 'Rekayasa Perangkat Lunak',
                'wali_kelas_id' => $guru2->id,
                'ruangan' => 'R-102',
                'kapasitas' => 36,
            ]
        );

        $kelas3 = Kelas::updateOrCreate(
            ['nama_kelas' => 'XI TKJ 1'],
            [
                'tingkat' => '11',
                'jurusan' => 'Teknik Komputer dan Jaringan',
                'wali_kelas_id' => $guru3->id,
                'ruangan' => 'R-103',
                'kapasitas' => 36,
            ]
        );

        // Update siswa user dengan kelas_id
        $siswaUser = User::where('email', 'siswa@smkn2.sch.id')->first();
        if ($siswaUser) {
            $siswaUser->kelas_id = $kelas1->id;
            $siswaUser->save();
        }
    }
}
