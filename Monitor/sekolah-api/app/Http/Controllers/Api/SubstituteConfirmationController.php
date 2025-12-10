<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\GuruPengganti;
use App\Models\SubstituteConfirmation;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Carbon\Carbon;

class SubstituteConfirmationController extends Controller
{
    /**
     * Daftar guru pengganti hari ini untuk kelas siswa dengan status konfirmasi
     */
    public function index(Request $request): JsonResponse
    {
        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        // Ambil guru pengganti hari ini untuk kelas user
        // Status: Disetujui atau Selesai (yang sudah disetujui kurikulum)
        $guruPengganti = GuruPengganti::with(['jadwal.guru', 'jadwal.kelas', 'guruAsli', 'guruPengganti', 'confirmations'])
            ->whereHas('jadwal', function ($query) use ($user) {
                $query->where('kelas_id', $user->kelas_id);
            })
            ->whereDate('tanggal', today())
            ->whereIn('status', ['Disetujui', 'Selesai'])
            ->orderBy('created_at', 'desc')
            ->get()
            ->map(function ($item) use ($user) {
                $userConfirmation = $item->confirmations->where('user_id', $user->id)->first();
                return [
                    'id' => $item->id,
                    'tanggal' => $item->tanggal->format('Y-m-d'),
                    'jadwal' => $item->jadwal ? [
                        'id' => $item->jadwal->id,
                        'hari' => $item->jadwal->hari,
                        'jam_mulai' => $item->jadwal->jam_mulai,
                        'jam_selesai' => $item->jadwal->jam_selesai,
                        'mata_pelajaran' => $item->jadwal->mata_pelajaran ?? null,
                        'kelas' => $item->jadwal->kelas ? [
                            'id' => $item->jadwal->kelas->id,
                            'nama' => $item->jadwal->kelas->nama_kelas,
                        ] : null,
                    ] : null,
                    'guru_asli' => $item->guruAsli ? [
                        'id' => $item->guruAsli->id,
                        'nama' => $item->guruAsli->nama,
                    ] : null,
                    'guru_pengganti' => $item->guruPengganti ? [
                        'id' => $item->guruPengganti->id,
                        'nama' => $item->guruPengganti->nama,
                    ] : null,
                    'alasan' => $item->alasan,
                    'keterangan' => $userConfirmation ? $userConfirmation->keterangan : null,
                    'status' => $item->status,
                    'sudah_dikonfirmasi' => $userConfirmation !== null,
                    'waktu_konfirmasi' => $userConfirmation ? $userConfirmation->confirmed_at->format('Y-m-d H:i:s') : null,
                    'jumlah_konfirmasi' => $item->confirmations->count(),
                ];
            });

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti hari ini berhasil diambil',
            'data' => $guruPengganti
        ]);
    }

    /**
     * Siswa konfirmasi bahwa guru pengganti sudah masuk kelas
     */
    public function store(Request $request): JsonResponse
    {
        $request->validate([
            'guru_pengganti_id' => 'required|exists:guru_pengganti,id',
            'keterangan' => 'nullable|string|max:500',
        ]);

        $user = $request->user();

        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Anda belum terdaftar di kelas manapun'
            ], 400);
        }

        $guruPengganti = GuruPengganti::with('jadwal')->find($request->guru_pengganti_id);

        // Pastikan guru pengganti untuk kelas user
        if (!$guruPengganti->jadwal || $guruPengganti->jadwal->kelas_id !== $user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti ini bukan untuk kelas Anda'
            ], 403);
        }

        // Pastikan hari ini
        if (!$guruPengganti->tanggal->isToday()) {
            return response()->json([
                'success' => false,
                'message' => 'Konfirmasi hanya bisa dilakukan pada tanggal yang sama'
            ], 400);
        }

        // Cek apakah sudah konfirmasi sebelumnya
        $existing = SubstituteConfirmation::where('guru_pengganti_id', $request->guru_pengganti_id)
            ->where('user_id', $user->id)
            ->first();

        if ($existing) {
            return response()->json([
                'success' => false,
                'message' => 'Anda sudah mengkonfirmasi guru pengganti ini'
            ], 400);
        }

        $confirmation = SubstituteConfirmation::create([
            'guru_pengganti_id' => $request->guru_pengganti_id,
            'user_id' => $user->id,
            'confirmed_at' => now(),
            'keterangan' => $request->keterangan,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Konfirmasi berhasil! Terima kasih telah mengkonfirmasi kehadiran guru pengganti.',
            'data' => [
                'id' => $confirmation->id,
                'guru_pengganti_id' => $confirmation->guru_pengganti_id,
                'user_id' => $confirmation->user_id,
                'confirmed_at' => $confirmation->confirmed_at->format('Y-m-d H:i:s'),
            ]
        ], 201);
    }

    /**
     * Daftar konfirmasi untuk guru pengganti tertentu
     */
    public function show(Request $request, $guruPenggantiId): JsonResponse
    {
        $guruPengganti = GuruPengganti::with(['jadwal.kelas', 'guruAsli', 'guruPengganti', 'confirmations.user'])
            ->find($guruPenggantiId);

        if (!$guruPengganti) {
            return response()->json([
                'success' => false,
                'message' => 'Data guru pengganti tidak ditemukan'
            ], 404);
        }

        $confirmations = $guruPengganti->confirmations->map(function ($confirmation) {
            return [
                'id' => $confirmation->id,
                'user' => [
                    'id' => $confirmation->user->id,
                    'name' => $confirmation->user->name,
                ],
                'confirmed_at' => $confirmation->confirmed_at->format('Y-m-d H:i:s'),
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data konfirmasi berhasil diambil',
            'data' => [
                'guru_pengganti' => [
                    'id' => $guruPengganti->id,
                    'tanggal' => $guruPengganti->tanggal->format('Y-m-d'),
                    'guru_asli' => $guruPengganti->guruAsli ? $guruPengganti->guruAsli->nama : null,
                    'guru_pengganti' => $guruPengganti->guruPengganti ? $guruPengganti->guruPengganti->nama : null,
                    'kelas' => $guruPengganti->jadwal && $guruPengganti->jadwal->kelas ? $guruPengganti->jadwal->kelas->nama_kelas : null,
                ],
                'confirmations' => $confirmations,
                'total_konfirmasi' => $confirmations->count(),
            ]
        ]);
    }
}
