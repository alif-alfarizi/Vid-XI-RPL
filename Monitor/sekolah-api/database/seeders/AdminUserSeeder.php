<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class AdminUserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create Super Admin
        User::firstOrCreate(
            ['email' => 'admin@sekolah.com'],
            [
                'nama' => 'Super Admin',
                'password' => Hash::make('admin123'),
                'role' => 'superadmin',
            ]
        );

        // Create Regular Admin
        User::firstOrCreate(
            ['email' => 'admin@admin.com'],
            [
                'nama' => 'Administrator',
                'password' => Hash::make('admin123'),
                'role' => 'admin',
            ]
        );

        $this->command->info('Admin users created successfully!');
        $this->command->info('Super Admin - Email: admin@sekolah.com | Password: admin123');
        $this->command->info('Admin - Email: admin@admin.com | Password: admin123');
    }
}
