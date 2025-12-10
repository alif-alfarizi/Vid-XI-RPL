<?php

namespace App\Http\Controllers;

use App\Models\GuruPengganti;
use App\Models\Guru;
use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class GuruPenggantiController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = GuruPengganti::with(['jadwal.guru', 'jadwal.kelas', 'guruAsli', 'guruPengganti', 'disetujuiOleh']);

        // Filter by status
        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        // Filter by alasan
        if ($request->filled('alasan')) {
            $query->where('alasan', $request->alasan);
        }

        // Filter by tanggal
        if ($request->filled('tanggal_dari')) {
            $query->whereDate('tanggal', '>=', $request->tanggal_dari);
        }
        if ($request->filled('tanggal_sampai')) {
            $query->whereDate('tanggal', '<=', $request->tanggal_sampai);
        }

        // Filter hari ini
        if ($request->has('hari_ini')) {
            $query->whereDate('tanggal', today());
        }

        $guruPengganti = $query->orderBy('tanggal', 'desc')->paginate(10);

        // Statistics
        $stats = [
            'total' => GuruPengganti::count(),
            'hari_ini' => GuruPengganti::whereDate('tanggal', today())->count(),
            'pending' => GuruPengganti::where('status', 'Pending')->count(),
            'disetujui' => GuruPengganti::where('status', 'Disetujui')->count(),
            'ditolak' => GuruPengganti::where('status', 'Ditolak')->count(),
            'bulan_ini' => GuruPengganti::whereMonth('tanggal', now()->month)
                ->whereYear('tanggal', now()->year)
                ->count(),
        ];

        return view('guru-pengganti.index', compact('guruPengganti', 'stats'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $jadwals = Jadwal::with(['guru', 'kelas'])->get();
        $gurus = Guru::orderBy('nama')->get();

        return view('guru-pengganti.create', compact('jadwals', 'gurus'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'tanggal' => 'required|date',
            'jadwal_id' => 'required|exists:jadwal,id',
            'guru_asli_id' => 'required|exists:guru,id',
            'guru_pengganti_id' => 'required|exists:guru,id|different:guru_asli_id',
            'alasan' => 'required|in:Sakit,Izin,Cuti,Dinas Luar,Lainnya',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:Pending,Disetujui,Ditolak,Selesai',
        ]);

        GuruPengganti::create($validated);

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Data guru pengganti berhasil ditambahkan!');
    }

    /**
     * Display the specified resource.
     */
    public function show(GuruPengganti $guruPengganti)
    {
        $guruPengganti->load(['jadwal.guru', 'jadwal.kelas', 'guruAsli', 'guruPengganti', 'disetujuiOleh']);

        return view('guru-pengganti.show', compact('guruPengganti'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(GuruPengganti $guruPengganti)
    {
        $jadwals = Jadwal::with(['guru', 'kelas'])->get();
        $gurus = Guru::orderBy('nama')->get();

        return view('guru-pengganti.edit', compact('guruPengganti', 'jadwals', 'gurus'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, GuruPengganti $guruPengganti)
    {
        $validated = $request->validate([
            'tanggal' => 'required|date',
            'jadwal_id' => 'required|exists:jadwal,id',
            'guru_asli_id' => 'required|exists:guru,id',
            'guru_pengganti_id' => 'required|exists:guru,id|different:guru_asli_id',
            'alasan' => 'required|in:Sakit,Izin,Cuti,Dinas Luar,Lainnya',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:Pending,Disetujui,Ditolak,Selesai',
        ]);

        $guruPengganti->update($validated);

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Data guru pengganti berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(GuruPengganti $guruPengganti)
    {
        $guruPengganti->delete();

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Data guru pengganti berhasil dihapus!');
    }

    /**
     * Approve a pending replacement request.
     */
    public function approve(GuruPengganti $guruPengganti)
    {
        $guruPengganti->update([
            'status' => 'Disetujui',
            'disetujui_oleh' => Auth::id(),
            'disetujui_pada' => now(),
        ]);

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Penggantian guru berhasil disetujui!');
    }

    /**
     * Reject a pending replacement request.
     */
    public function reject(GuruPengganti $guruPengganti)
    {
        $guruPengganti->update([
            'status' => 'Ditolak',
            'disetujui_oleh' => Auth::id(),
            'disetujui_pada' => now(),
        ]);

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Penggantian guru berhasil ditolak!');
    }

    /**
     * Mark replacement as completed.
     */
    public function complete(GuruPengganti $guruPengganti)
    {
        $guruPengganti->update([
            'status' => 'Selesai',
        ]);

        return redirect()->route('guru-pengganti.index')
            ->with('success', 'Penggantian guru ditandai selesai!');
    }

    /**
     * Get guru from jadwal (AJAX)
     */
    public function getGuruFromJadwal(Jadwal $jadwal)
    {
        return response()->json([
            'guru_id' => $jadwal->guru_id,
            'guru_nama' => $jadwal->guru->nama ?? 'N/A',
        ]);
    }
}
