<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Guru;
use App\Models\Jadwal;
use App\Models\TeacherAttendance;
use App\Models\Kelas;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;

class KepalaSekolahController extends Controller
{
    /**
     * Dashboard Executive - Ringkasan KPI Sekolah
     */
    public function dashboard(Request $request)
    {
        $today = Carbon::today();
        $thisMonth = Carbon::now()->month;
        $thisYear = Carbon::now()->year;
        $lastMonth = Carbon::now()->subMonth();

        // Total Guru
        $totalGuru = Guru::count();

        // Kehadiran Hari Ini
        $kehadiranHariIni = TeacherAttendance::whereDate('tanggal', $today)->get();
        $hadirHariIni = $kehadiranHariIni->where('status', 'Hadir')->count();
        $tidakHadirHariIni = $kehadiranHariIni->where('status', 'Tidak Hadir')->count();
        $izinHariIni = $kehadiranHariIni->where('status', 'Izin')->count();

        // Jadwal Aktif Hari Ini
        $hariIni = $this->getHariIndonesia($today->dayOfWeek);
        $jadwalHariIni = Jadwal::where('hari', $hariIni)->count();

        // Persentase Kehadiran Bulan Ini
        $kehadiranBulanIni = TeacherAttendance::whereMonth('tanggal', $thisMonth)
            ->whereYear('tanggal', $thisYear)
            ->get();
        $totalKehadiranBulanIni = $kehadiranBulanIni->count();
        $hadirBulanIni = $kehadiranBulanIni->where('status', 'Hadir')->count();
        $persentaseBulanIni = $totalKehadiranBulanIni > 0
            ? round(($hadirBulanIni / $totalKehadiranBulanIni) * 100, 1)
            : 0;

        // Persentase Kehadiran Bulan Lalu (untuk perbandingan)
        $kehadiranBulanLalu = TeacherAttendance::whereMonth('tanggal', $lastMonth->month)
            ->whereYear('tanggal', $lastMonth->year)
            ->get();
        $totalKehadiranBulanLalu = $kehadiranBulanLalu->count();
        $hadirBulanLalu = $kehadiranBulanLalu->where('status', 'Hadir')->count();
        $persentaseBulanLalu = $totalKehadiranBulanLalu > 0
            ? round(($hadirBulanLalu / $totalKehadiranBulanLalu) * 100, 1)
            : 0;

        // Selisih persentase
        $selisihPersentase = $persentaseBulanIni - $persentaseBulanLalu;

        // Total Kelas
        $totalKelas = Kelas::count();

        // Total Jadwal
        $totalJadwal = Jadwal::count();

        return response()->json([
            'success' => true,
            'data' => [
                'summary' => [
                    'total_guru' => $totalGuru,
                    'total_kelas' => $totalKelas,
                    'total_jadwal' => $totalJadwal,
                    'jadwal_hari_ini' => $jadwalHariIni,
                ],
                'kehadiran_hari_ini' => [
                    'hadir' => $hadirHariIni,
                    'tidak_hadir' => $tidakHadirHariIni,
                    'izin' => $izinHariIni,
                    'total' => $hadirHariIni + $tidakHadirHariIni + $izinHariIni,
                    'persentase_hadir' => ($hadirHariIni + $tidakHadirHariIni + $izinHariIni) > 0
                        ? round(($hadirHariIni / ($hadirHariIni + $tidakHadirHariIni + $izinHariIni)) * 100, 1)
                        : 0,
                ],
                'performa_bulanan' => [
                    'bulan_ini' => [
                        'bulan' => Carbon::now()->translatedFormat('F Y'),
                        'persentase' => $persentaseBulanIni,
                        'total_hadir' => $hadirBulanIni,
                        'total_kehadiran' => $totalKehadiranBulanIni,
                    ],
                    'bulan_lalu' => [
                        'bulan' => $lastMonth->translatedFormat('F Y'),
                        'persentase' => $persentaseBulanLalu,
                    ],
                    'selisih' => $selisihPersentase,
                    'trend' => $selisihPersentase >= 0 ? 'naik' : 'turun',
                ],
                'tanggal' => $today->format('Y-m-d'),
                'hari' => $hariIni,
            ]
        ]);
    }

    /**
     * Statistik Tren Kehadiran Mingguan
     */
    public function getTrenMingguan(Request $request)
    {
        $endDate = Carbon::today();
        $startDate = Carbon::today()->subDays(6);

        $trenData = [];

        for ($date = $startDate->copy(); $date <= $endDate; $date->addDay()) {
            $kehadiran = TeacherAttendance::whereDate('tanggal', $date)->get();
            $total = $kehadiran->count();
            $hadir = $kehadiran->where('status', 'Hadir')->count();

            $trenData[] = [
                'tanggal' => $date->format('Y-m-d'),
                'hari' => $this->getHariIndonesia($date->dayOfWeek),
                'hari_singkat' => $this->getHariSingkat($date->dayOfWeek),
                'hadir' => $hadir,
                'tidak_hadir' => $kehadiran->where('status', 'Tidak Hadir')->count(),
                'izin' => $kehadiran->where('status', 'Izin')->count(),
                'total' => $total,
                'persentase' => $total > 0 ? round(($hadir / $total) * 100, 1) : 0,
            ];
        }

        // Rata-rata mingguan
        $totalPersentase = array_sum(array_column($trenData, 'persentase'));
        $rataRata = count($trenData) > 0 ? round($totalPersentase / count($trenData), 1) : 0;

        return response()->json([
            'success' => true,
            'data' => [
                'tren' => $trenData,
                'rata_rata' => $rataRata,
                'periode' => [
                    'mulai' => $startDate->format('Y-m-d'),
                    'selesai' => $endDate->format('Y-m-d'),
                ]
            ]
        ]);
    }

    /**
     * Statistik Tren Kehadiran Bulanan
     */
    public function getTrenBulanan(Request $request)
    {
        $year = $request->input('tahun', Carbon::now()->year);

        $trenData = [];

        for ($month = 1; $month <= 12; $month++) {
            $kehadiran = TeacherAttendance::whereMonth('tanggal', $month)
                ->whereYear('tanggal', $year)
                ->get();

            $total = $kehadiran->count();
            $hadir = $kehadiran->where('status', 'Hadir')->count();

            $trenData[] = [
                'bulan' => $month,
                'nama_bulan' => Carbon::create($year, $month, 1)->translatedFormat('F'),
                'nama_bulan_singkat' => Carbon::create($year, $month, 1)->translatedFormat('M'),
                'hadir' => $hadir,
                'tidak_hadir' => $kehadiran->where('status', 'Tidak Hadir')->count(),
                'izin' => $kehadiran->where('status', 'Izin')->count(),
                'total' => $total,
                'persentase' => $total > 0 ? round(($hadir / $total) * 100, 1) : 0,
            ];
        }

        return response()->json([
            'success' => true,
            'data' => [
                'tahun' => $year,
                'tren' => $trenData,
            ]
        ]);
    }

    /**
     * Ranking Kehadiran Guru
     */
    public function getRankingGuru(Request $request)
    {
        $bulan = $request->input('bulan', Carbon::now()->month);
        $tahun = $request->input('tahun', Carbon::now()->year);
        $limit = $request->input('limit', 10);

        $gurus = Guru::all();
        $rankingData = [];

        foreach ($gurus as $guru) {
            $kehadiran = TeacherAttendance::where('guru_id', $guru->id)
                ->whereMonth('tanggal', $bulan)
                ->whereYear('tanggal', $tahun)
                ->get();

            $total = $kehadiran->count();
            $hadir = $kehadiran->where('status', 'Hadir')->count();
            $tidakHadir = $kehadiran->where('status', 'Tidak Hadir')->count();
            $izin = $kehadiran->where('status', 'Izin')->count();
            $persentase = $total > 0 ? round(($hadir / $total) * 100, 1) : 0;

            $rankingData[] = [
                'guru_id' => $guru->id,
                'nama' => $guru->nama,
                'nip' => $guru->nip,
                'mata_pelajaran' => $guru->mata_pelajaran ?? '-',
                'hadir' => $hadir,
                'tidak_hadir' => $tidakHadir,
                'izin' => $izin,
                'total' => $total,
                'persentase' => $persentase,
            ];
        }

        // Sort by persentase descending
        usort($rankingData, function ($a, $b) {
            return $b['persentase'] <=> $a['persentase'];
        });

        // Add ranking
        foreach ($rankingData as $index => &$data) {
            $data['ranking'] = $index + 1;
        }

        // Get top performers
        $topPerformers = array_slice($rankingData, 0, 5);

        // Get bottom performers (yang perlu perhatian)
        $bottomPerformers = array_slice(array_reverse($rankingData), 0, 5);

        return response()->json([
            'success' => true,
            'data' => [
                'periode' => [
                    'bulan' => Carbon::create($tahun, $bulan, 1)->translatedFormat('F'),
                    'tahun' => $tahun,
                ],
                'ranking' => array_slice($rankingData, 0, $limit),
                'top_performers' => $topPerformers,
                'need_attention' => $bottomPerformers,
                'total_guru' => count($rankingData),
            ]
        ]);
    }

    /**
     * Laporan Ringkasan
     */
    public function getLaporan(Request $request)
    {
        $bulan = $request->input('bulan', Carbon::now()->month);
        $tahun = $request->input('tahun', Carbon::now()->year);

        // Data kehadiran bulan ini
        $kehadiran = TeacherAttendance::whereMonth('tanggal', $bulan)
            ->whereYear('tanggal', $tahun)
            ->get();

        $totalKehadiran = $kehadiran->count();
        $hadir = $kehadiran->where('status', 'Hadir')->count();
        $tidakHadir = $kehadiran->where('status', 'Tidak Hadir')->count();
        $izin = $kehadiran->where('status', 'Izin')->count();

        // Hari dengan kehadiran tertinggi
        $kehadiranPerHari = $kehadiran->groupBy(function ($item) {
            return Carbon::parse($item->tanggal)->dayOfWeek;
        })->map(function ($items, $day) {
            $total = $items->count();
            $hadir = $items->where('status', 'Hadir')->count();
            return [
                'hari' => $this->getHariIndonesia($day),
                'total' => $total,
                'hadir' => $hadir,
                'persentase' => $total > 0 ? round(($hadir / $total) * 100, 1) : 0,
            ];
        })->values()->sortByDesc('persentase')->values();

        // Guru dengan kehadiran terbaik
        $guruTerbaik = Guru::select('guru.*')
            ->selectRaw('(SELECT COUNT(*) FROM teacher_attendances WHERE teacher_attendances.guru_id = guru.id AND status = "Hadir" AND MONTH(tanggal) = ? AND YEAR(tanggal) = ?) as total_hadir', [$bulan, $tahun])
            ->orderByDesc('total_hadir')
            ->limit(3)
            ->get();

        // Guru yang perlu perhatian
        $guruPerhatian = Guru::select('guru.*')
            ->selectRaw('(SELECT COUNT(*) FROM teacher_attendances WHERE teacher_attendances.guru_id = guru.id AND status = "Tidak Hadir" AND MONTH(tanggal) = ? AND YEAR(tanggal) = ?) as total_tidak_hadir', [$bulan, $tahun])
            ->orderByDesc('total_tidak_hadir')
            ->limit(3)
            ->get();

        return response()->json([
            'success' => true,
            'data' => [
                'periode' => [
                    'bulan' => Carbon::create($tahun, $bulan, 1)->translatedFormat('F Y'),
                    'bulan_angka' => $bulan,
                    'tahun' => $tahun,
                ],
                'ringkasan' => [
                    'total_kehadiran' => $totalKehadiran,
                    'hadir' => $hadir,
                    'tidak_hadir' => $tidakHadir,
                    'izin' => $izin,
                    'persentase_hadir' => $totalKehadiran > 0
                        ? round(($hadir / $totalKehadiran) * 100, 1)
                        : 0,
                ],
                'kehadiran_per_hari' => $kehadiranPerHari,
                'guru_terbaik' => $guruTerbaik->map(function ($guru) {
                    return [
                        'id' => $guru->id,
                        'nama' => $guru->nama,
                        'total_hadir' => $guru->total_hadir,
                    ];
                }),
                'guru_perhatian' => $guruPerhatian->map(function ($guru) {
                    return [
                        'id' => $guru->id,
                        'nama' => $guru->nama,
                        'total_tidak_hadir' => $guru->total_tidak_hadir,
                    ];
                }),
            ]
        ]);
    }

    /**
     * Get Hari Indonesia
     */
    private function getHariIndonesia($dayOfWeek)
    {
        $hari = [
            0 => 'Minggu',
            1 => 'Senin',
            2 => 'Selasa',
            3 => 'Rabu',
            4 => 'Kamis',
            5 => 'Jumat',
            6 => 'Sabtu',
        ];
        return $hari[$dayOfWeek] ?? '';
    }

    /**
     * Get Hari Singkat
     */
    private function getHariSingkat($dayOfWeek)
    {
        $hari = [
            0 => 'Min',
            1 => 'Sen',
            2 => 'Sel',
            3 => 'Rab',
            4 => 'Kam',
            5 => 'Jum',
            6 => 'Sab',
        ];
        return $hari[$dayOfWeek] ?? '';
    }
}
