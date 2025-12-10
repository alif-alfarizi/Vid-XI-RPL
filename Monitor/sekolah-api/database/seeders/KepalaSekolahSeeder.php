<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class KepalaSekolahSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        User::updateOrCreate(
            ['email' => 'kepsek@smkn2.sch.id'],
            [
                'nama' => 'Drs. Hendra Wijaya, M.Pd',
                'email' => 'kepsek@smkn2.sch.id',
                'password' => Hash::make('password'),
                'role' => 'Kepala Sekolah',
            ]
        );

        $this->command->info('Kepala Sekolah berhasil ditambahkan!');
        $this->command->info('Login: kepsek@smkn2.sch.id / password');
    }
}
