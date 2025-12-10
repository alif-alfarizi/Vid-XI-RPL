<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Carbon\Carbon;

class SiswaController extends Controller
{
    /**
     * Get jadwal berdasarkan kelas user yang login
     */
    public function getJadwal(Request $request): JsonResponse
    {
        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        $jadwals = Jadwal::with(['guru', 'kelas'])
            ->where('kelas_id', $user->kelas_id)
            ->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
            ->orderBy('jam_mulai')
            ->get()
            ->groupBy('hari');

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diambil',
            'data' => [
                'kelas' => $user->kelas,
                'jadwal' => $jadwals
            ]
        ]);
    }

    /**
     * Get jadwal hari ini
     */
    public function getJadwalHariIni(Request $request): JsonResponse
    {
        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        // Get hari dalam bahasa Indonesia
        $hariMapping = [
            'Sunday' => 'Minggu',
            'Monday' => 'Senin',
            'Tuesday' => 'Selasa',
            'Wednesday' => 'Rabu',
            'Thursday' => 'Kamis',
            'Friday' => 'Jumat',
            'Saturday' => 'Sabtu'
        ];

        $hariIni = $hariMapping[Carbon::now()->format('l')];

        $jadwals = Jadwal::with(['guru', 'kelas'])
            ->where('kelas_id', $user->kelas_id)
            ->where('hari', $hariIni)
            ->orderBy('jam_mulai')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Jadwal hari ini berhasil diambil',
            'data' => [
                'hari' => $hariIni,
                'tanggal' => Carbon::now()->format('d F Y'),
                'kelas' => $user->kelas,
                'jadwal' => $jadwals
            ]
        ]);
    }
}
