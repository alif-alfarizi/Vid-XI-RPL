<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\TeacherAttendance;
use App\Models\Guru;
use App\Models\GuruPengganti;
use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Validator;
use Carbon\Carbon;

class TeacherAttendanceApiController extends Controller
{
    /**
     * Get all teacher attendance records
     */
    public function index(Request $request): JsonResponse
    {
        $query = TeacherAttendance::with('guru');

        // Filter by date if provided
        if ($request->has('tanggal')) {
            $query->where('tanggal', $request->tanggal);
        }

        // Filter by guru if provided
        if ($request->has('guru_id')) {
            $query->where('guru_id', $request->guru_id);
        }

        $attendances = $query->orderBy('tanggal', 'desc')
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Data kehadiran guru berhasil diambil',
            'data' => $attendances
        ]);
    }

    /**
     * Get today's attendance
     */
    public function today(): JsonResponse
    {
        $today = Carbon::now()->format('Y-m-d');

        $attendances = TeacherAttendance::with('guru')
            ->where('tanggal', $today)
            ->get();

        // Get all gurus and mark which ones have attendance
        $allGurus = Guru::all();
        $attendedGuruIds = $attendances->pluck('guru_id')->toArray();

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

        // Get guru pengganti untuk hari ini
        $guruPengganti = GuruPengganti::with(['jadwal.kelas', 'guruAsli', 'guruPengganti'])
            ->where('tanggal', $today)
            ->where('status', 'disetujui')
            ->get();

        // Get jadwal hari ini untuk melihat guru yang seharusnya mengajar
        $jadwalHariIni = Jadwal::with(['guru', 'kelas'])
            ->where('hari', $hariIni)
            ->get();

        $guruStatus = $allGurus->map(function ($guru) use ($attendances, $attendedGuruIds, $guruPengganti, $jadwalHariIni) {
            $attendance = $attendances->firstWhere('guru_id', $guru->id);

            // Cek apakah guru ini digantikan hari ini
            $digantikan = $guruPengganti->firstWhere('guru_asli_id', $guru->id);

            // Cek apakah guru ini menggantikan guru lain
            $menggantikan = $guruPengganti->firstWhere('guru_pengganti_id', $guru->id);

            // Cek jadwal mengajar hari ini
            $jadwalMengajar = $jadwalHariIni->where('guru_id', $guru->id)->values()->all();

            return [
                'guru' => $guru,
                'has_attendance' => in_array($guru->id, $attendedGuruIds),
                'attendance' => $attendance,
                'jadwal_mengajar' => $jadwalMengajar,
                'digantikan' => $digantikan ? [
                    'id' => $digantikan->id,
                    'alasan' => $digantikan->alasan,
                    'keterangan' => $digantikan->keterangan,
                    'guru_pengganti' => $digantikan->guruPengganti,
                    'jadwal' => $digantikan->jadwal
                ] : null,
                'menggantikan' => $menggantikan ? [
                    'id' => $menggantikan->id,
                    'alasan' => $menggantikan->alasan,
                    'keterangan' => $menggantikan->keterangan,
                    'guru_asli' => $menggantikan->guruAsli,
                    'jadwal' => $menggantikan->jadwal
                ] : null
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data kehadiran hari ini berhasil diambil',
            'data' => [
                'tanggal' => $today,
                'tanggal_formatted' => Carbon::now()->format('d F Y'),
                'hari' => $hariIni,
                'summary' => [
                    'total_guru' => $allGurus->count(),
                    'hadir' => $attendances->where('status', 'Hadir')->count(),
                    'terlambat' => $attendances->where('status', 'Terlambat')->count(),
                    'tidak_hadir' => $attendances->where('status', 'Tidak Hadir')->count(),
                    'izin' => $attendances->where('status', 'Izin')->count(),
                    'belum_input' => $allGurus->count() - $attendances->count()
                ],
                'guru_status' => $guruStatus,
                'attendances' => $attendances,
                'guru_pengganti' => $guruPengganti
            ]
        ]);
    }

    /**
     * Store a new teacher attendance
     */
    public function store(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'guru_id' => 'required|exists:guru,id',
            'status' => 'required|in:Hadir,Terlambat,Tidak Hadir,Izin',
            'tanggal' => 'nullable|date',
            'jam_masuk' => 'nullable|date_format:H:i',
            'keterangan' => 'nullable|string|max:500'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $tanggal = $request->tanggal ?? Carbon::now()->format('Y-m-d');

        // Check if attendance already exists for this guru on this date
        $existing = TeacherAttendance::where('guru_id', $request->guru_id)
            ->where('tanggal', $tanggal)
            ->first();

        if ($existing) {
            // Update existing
            $existing->update([
                'status' => $request->status,
                'jam_masuk' => $request->jam_masuk ?? ($request->status === 'Hadir' || $request->status === 'Terlambat' ? Carbon::now()->format('H:i') : null),
                'keterangan' => $request->keterangan
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Kehadiran guru berhasil diupdate',
                'data' => $existing->load('guru')
            ]);
        }

        // Create new
        $attendance = TeacherAttendance::create([
            'guru_id' => $request->guru_id,
            'tanggal' => $tanggal,
            'status' => $request->status,
            'jam_masuk' => $request->jam_masuk ?? ($request->status === 'Hadir' || $request->status === 'Terlambat' ? Carbon::now()->format('H:i') : null),
            'keterangan' => $request->keterangan
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Kehadiran guru berhasil dicatat',
            'data' => $attendance->load('guru')
        ], 201);
    }

    /**
     * Get all gurus
     */
    public function getGurus(): JsonResponse
    {
        $gurus = Guru::orderBy('nama')->get();

        return response()->json([
            'success' => true,
            'message' => 'Data guru berhasil diambil',
            'data' => $gurus
        ]);
    }
}
