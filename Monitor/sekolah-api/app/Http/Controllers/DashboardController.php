<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Guru;
use App\Models\Kelas;
use App\Models\Jadwal;
use App\Models\TeacherAttendance;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Carbon\Carbon;

class DashboardController extends Controller
{
    /**
     * Display the dashboard
     */
    public function index()
    {
        try {
            // Fast queries dengan timeout protection
            $total_users = User::count();
            $total_guru = Guru::count();
            $total_kelas = Kelas::count();
            $total_jadwal = Jadwal::count();

            // Teacher Attendance Statistics for Today
            $today = Carbon::today();
            $attendance_stats = [
                'hadir' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Hadir')->count(),
                'tidak_hadir' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Tidak Hadir')->count(),
                'terlambat' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Terlambat')->count(),
                'izin' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Izin')->count(),
            ];

            // Recent data - simplified
            $recent_jadwal = Jadwal::with(['kelas:id,nama_kelas', 'guru:id,nama'])
                ->select('id', 'kelas_id', 'guru_id', 'mata_pelajaran', 'hari', 'jam_mulai', 'jam_selesai')
                ->orderBy('id', 'desc')
                ->limit(5)
                ->get();

            $recent_guru = Guru::select('id', 'nama', 'nip', 'mata_pelajaran')
                ->orderBy('id', 'desc')
                ->limit(5)
                ->get();

            // Recent Attendance
            $recent_attendance = TeacherAttendance::with('guru:id,nama,nip')
                ->orderBy('created_at', 'desc')
                ->limit(5)
                ->get();

            return view('dashboard', compact(
                'total_users',
                'total_guru',
                'total_kelas',
                'total_jadwal',
                'recent_jadwal',
                'recent_guru',
                'attendance_stats',
                'recent_attendance'
            ));
        } catch (\Exception $e) {
            // Jika error, langsung return view dengan data kosong
            Log::error('Dashboard error: ' . $e->getMessage());

            return view('dashboard', [
                'total_users' => 0,
                'total_guru' => 0,
                'total_kelas' => 0,
                'total_jadwal' => 0,
                'recent_jadwal' => collect([]),
                'recent_guru' => collect([]),
                'attendance_stats' => ['hadir' => 0, 'tidak_hadir' => 0, 'terlambat' => 0, 'izin' => 0],
                'recent_attendance' => collect([])
            ]);
        }
    }
}
