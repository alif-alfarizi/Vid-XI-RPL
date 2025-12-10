<?php

namespace App\Http\Controllers;

use App\Models\TeacherAttendance;
use App\Models\Guru;
use Illuminate\Http\Request;
use Carbon\Carbon;

class TeacherAttendanceController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = TeacherAttendance::with('guru')->orderBy('tanggal', 'desc');

        // Filter by date
        if ($request->has('tanggal') && $request->tanggal) {
            $query->whereDate('tanggal', $request->tanggal);
        }

        // Filter by status
        if ($request->has('status') && $request->status) {
            $query->where('status', $request->status);
        }

        // Filter by guru
        if ($request->has('guru_id') && $request->guru_id) {
            $query->where('guru_id', $request->guru_id);
        }

        $attendances = $query->paginate(15);
        $gurus = Guru::orderBy('nama', 'asc')->get();

        // Statistics for today
        $today = Carbon::today();
        $stats = [
            'total_guru' => Guru::count(),
            'hadir' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Hadir')->count(),
            'tidak_hadir' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Tidak Hadir')->count(),
            'terlambat' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Terlambat')->count(),
            'izin' => TeacherAttendance::whereDate('tanggal', $today)->where('status', 'Izin')->count(),
        ];

        return view('teacher-attendance.index', compact('attendances', 'gurus', 'stats'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $gurus = Guru::orderBy('nama', 'asc')->get();
        return view('teacher-attendance.create', compact('gurus'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $request->validate([
            'guru_id' => 'required|exists:guru,id',
            'tanggal' => 'required|date',
            'status' => 'required|in:Hadir,Tidak Hadir,Terlambat,Izin',
            'jam_masuk' => 'nullable|date_format:H:i',
            'jam_keluar' => 'nullable|date_format:H:i',
            'keterangan' => 'nullable|string|max:500',
        ], [
            'guru_id.required' => 'Guru harus dipilih.',
            'guru_id.exists' => 'Guru tidak ditemukan.',
            'tanggal.required' => 'Tanggal harus diisi.',
            'status.required' => 'Status kehadiran harus dipilih.',
            'status.in' => 'Status kehadiran tidak valid.',
        ]);

        // Check if attendance already exists for this guru on this date
        $exists = TeacherAttendance::where('guru_id', $request->guru_id)
            ->whereDate('tanggal', $request->tanggal)
            ->exists();

        if ($exists) {
            return back()->withErrors(['guru_id' => 'Kehadiran guru ini pada tanggal tersebut sudah tercatat.'])->withInput();
        }

        TeacherAttendance::create($request->all());

        return redirect()->route('teacher-attendance.index')
            ->with('success', 'Data kehadiran guru berhasil ditambahkan!');
    }

    /**
     * Display the specified resource.
     */
    public function show(TeacherAttendance $teacherAttendance)
    {
        $teacherAttendance->load('guru');
        return view('teacher-attendance.show', compact('teacherAttendance'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(TeacherAttendance $teacherAttendance)
    {
        $gurus = Guru::orderBy('nama', 'asc')->get();
        return view('teacher-attendance.edit', compact('teacherAttendance', 'gurus'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, TeacherAttendance $teacherAttendance)
    {
        $request->validate([
            'guru_id' => 'required|exists:guru,id',
            'tanggal' => 'required|date',
            'status' => 'required|in:Hadir,Tidak Hadir,Terlambat,Izin',
            'jam_masuk' => 'nullable|date_format:H:i',
            'jam_keluar' => 'nullable|date_format:H:i',
            'keterangan' => 'nullable|string|max:500',
        ], [
            'guru_id.required' => 'Guru harus dipilih.',
            'guru_id.exists' => 'Guru tidak ditemukan.',
            'tanggal.required' => 'Tanggal harus diisi.',
            'status.required' => 'Status kehadiran harus dipilih.',
            'status.in' => 'Status kehadiran tidak valid.',
        ]);

        // Check if attendance already exists for this guru on this date (excluding current record)
        $exists = TeacherAttendance::where('guru_id', $request->guru_id)
            ->whereDate('tanggal', $request->tanggal)
            ->where('id', '!=', $teacherAttendance->id)
            ->exists();

        if ($exists) {
            return back()->withErrors(['guru_id' => 'Kehadiran guru ini pada tanggal tersebut sudah tercatat.'])->withInput();
        }

        $teacherAttendance->update($request->all());

        return redirect()->route('teacher-attendance.index')
            ->with('success', 'Data kehadiran guru berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(TeacherAttendance $teacherAttendance)
    {
        $teacherAttendance->delete();

        return redirect()->route('teacher-attendance.index')
            ->with('success', 'Data kehadiran guru berhasil dihapus!');
    }

    /**
     * Bulk attendance entry for all teachers
     */
    public function bulkCreate()
    {
        $gurus = Guru::orderBy('nama', 'asc')->get();
        $today = Carbon::today()->format('Y-m-d');

        // Get already recorded attendance for today
        $recordedToday = TeacherAttendance::whereDate('tanggal', Carbon::today())
            ->pluck('guru_id')
            ->toArray();

        return view('teacher-attendance.bulk-create', compact('gurus', 'today', 'recordedToday'));
    }

    /**
     * Store bulk attendance
     */
    public function bulkStore(Request $request)
    {
        $request->validate([
            'tanggal' => 'required|date',
            'attendances' => 'required|array',
            'attendances.*.guru_id' => 'required|exists:guru,id',
            'attendances.*.status' => 'required|in:Hadir,Tidak Hadir,Terlambat,Izin',
        ]);

        $tanggal = $request->tanggal;
        $count = 0;

        foreach ($request->attendances as $attendance) {
            // Skip if already exists
            $exists = TeacherAttendance::where('guru_id', $attendance['guru_id'])
                ->whereDate('tanggal', $tanggal)
                ->exists();

            if (!$exists) {
                TeacherAttendance::create([
                    'guru_id' => $attendance['guru_id'],
                    'tanggal' => $tanggal,
                    'status' => $attendance['status'],
                    'jam_masuk' => $attendance['jam_masuk'] ?? null,
                    'jam_keluar' => $attendance['jam_keluar'] ?? null,
                    'keterangan' => $attendance['keterangan'] ?? null,
                ]);
                $count++;
            }
        }

        return redirect()->route('teacher-attendance.index')
            ->with('success', "Berhasil menambahkan {$count} data kehadiran guru!");
    }

    /**
     * Report page
     */
    public function report(Request $request)
    {
        $gurus = Guru::orderBy('nama', 'asc')->get();

        $startDate = $request->start_date ?? Carbon::now()->startOfMonth()->format('Y-m-d');
        $endDate = $request->end_date ?? Carbon::now()->format('Y-m-d');
        $guruId = $request->guru_id;

        $query = TeacherAttendance::with('guru')
            ->whereBetween('tanggal', [$startDate, $endDate]);

        if ($guruId) {
            $query->where('guru_id', $guruId);
        }

        $attendances = $query->orderBy('tanggal', 'desc')->get();

        // Summary statistics
        $summary = [
            'total' => $attendances->count(),
            'hadir' => $attendances->where('status', 'Hadir')->count(),
            'tidak_hadir' => $attendances->where('status', 'Tidak Hadir')->count(),
            'terlambat' => $attendances->where('status', 'Terlambat')->count(),
            'izin' => $attendances->where('status', 'Izin')->count(),
        ];

        // Per guru statistics
        $guruStats = [];
        if (!$guruId) {
            foreach ($gurus as $guru) {
                $guruAttendances = $attendances->where('guru_id', $guru->id);
                if ($guruAttendances->count() > 0) {
                    $guruStats[] = [
                        'nama' => $guru->nama,
                        'nip' => $guru->nip,
                        'hadir' => $guruAttendances->where('status', 'Hadir')->count(),
                        'tidak_hadir' => $guruAttendances->where('status', 'Tidak Hadir')->count(),
                        'terlambat' => $guruAttendances->where('status', 'Terlambat')->count(),
                        'izin' => $guruAttendances->where('status', 'Izin')->count(),
                    ];
                }
            }
        }

        return view('teacher-attendance.report', compact('attendances', 'gurus', 'summary', 'startDate', 'endDate', 'guruId', 'guruStats'));
    }
}
