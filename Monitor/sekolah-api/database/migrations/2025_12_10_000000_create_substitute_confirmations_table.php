<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('substitute_confirmations', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('guru_pengganti_id');
            $table->unsignedBigInteger('user_id');
            $table->timestamp('confirmed_at')->useCurrent();
            $table->timestamps();

            $table->foreign('guru_pengganti_id')->references('id')->on('guru_pengganti')->onDelete('cascade');
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');

            $table->unique(['guru_pengganti_id', 'user_id']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('substitute_confirmations');
    }
};
