<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        // Add indexes to jadwal table for better performance
        Schema::table('jadwal', function (Blueprint $table) {
            $table->index('kelas_id', 'idx_jadwal_kelas');
            $table->index('guru_id', 'idx_jadwal_guru');
            $table->index('hari', 'idx_jadwal_hari');
            $table->index(['hari', 'jam_mulai'], 'idx_jadwal_hari_jam');
        });

        // Add indexes to guru table
        Schema::table('guru', function (Blueprint $table) {
            $table->index('nama', 'idx_guru_nama');
            $table->index('nip', 'idx_guru_nip');
        });

        // Add indexes to kelas table
        Schema::table('kelas', function (Blueprint $table) {
            $table->index('tingkat', 'idx_kelas_tingkat');
            $table->index('wali_kelas_id', 'idx_kelas_wali');
        });

        // Add indexes to users table
        Schema::table('users', function (Blueprint $table) {
            $table->index('role', 'idx_users_role');
            $table->index('created_at', 'idx_users_created');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('jadwal', function (Blueprint $table) {
            $table->dropIndex('idx_jadwal_kelas');
            $table->dropIndex('idx_jadwal_guru');
            $table->dropIndex('idx_jadwal_hari');
            $table->dropIndex('idx_jadwal_hari_jam');
        });

        Schema::table('guru', function (Blueprint $table) {
            $table->dropIndex('idx_guru_nama');
            $table->dropIndex('idx_guru_nip');
        });

        Schema::table('kelas', function (Blueprint $table) {
            $table->dropIndex('idx_kelas_tingkat');
            $table->dropIndex('idx_kelas_wali');
        });

        Schema::table('users', function (Blueprint $table) {
            $table->dropIndex('idx_users_role');
            $table->dropIndex('idx_users_created');
        });
    }
};
