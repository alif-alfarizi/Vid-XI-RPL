<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('guru_pengganti', function (Blueprint $table) {
            $table->id();
            $table->date('tanggal');
            $table->foreignId('jadwal_id')->constrained('jadwal')->onDelete('cascade');
            $table->foreignId('guru_asli_id')->constrained('guru')->onDelete('cascade');
            $table->foreignId('guru_pengganti_id')->constrained('guru')->onDelete('cascade');
            $table->enum('alasan', ['Sakit', 'Izin', 'Cuti', 'Dinas Luar', 'Lainnya'])->default('Izin');
            $table->text('keterangan')->nullable();
            $table->enum('status', ['Pending', 'Disetujui', 'Ditolak', 'Selesai'])->default('Pending');
            $table->foreignId('disetujui_oleh')->nullable()->constrained('users')->onDelete('set null');
            $table->timestamp('disetujui_pada')->nullable();
            $table->timestamps();

            // Index untuk pencarian cepat
            $table->index(['tanggal', 'jadwal_id']);
            $table->index(['guru_asli_id', 'tanggal']);
            $table->index(['guru_pengganti_id', 'tanggal']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('guru_pengganti');
    }
};
