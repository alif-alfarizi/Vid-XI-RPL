<?php

namespace App\Http\Controllers;

use App\Models\Jadwal;
use App\Models\Kelas;
use App\Models\Guru;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class JadwalController extends Controller
{
    // Web Methods
    public function index()
    {
        $jadwals = Jadwal::with([
            'kelas:id,nama_kelas',
            'guru:id,nama,nip'
        ])
            ->select('id', 'kelas_id', 'guru_id', 'mata_pelajaran', 'hari', 'jam_mulai', 'jam_selesai', 'ruangan')
            ->orderBy('hari', 'asc')
            ->orderBy('jam_mulai', 'asc')
            ->paginate(20);

        return view('jadwal.index', compact('jadwals'));
    }

    public function create()
    {
        $kelas = Kelas::select('id', 'nama_kelas', 'tingkat')
            ->orderBy('nama_kelas', 'asc')
            ->get();
        $gurus = Guru::select('id', 'nama', 'nip', 'mata_pelajaran')
            ->orderBy('nama', 'asc')
            ->get();
        return view('jadwal.create', compact('kelas', 'gurus'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:guru,id',
            'mata_pelajaran' => 'required|string|max:255',
            'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_mulai' => 'required',
            'jam_selesai' => 'required',
            'ruangan' => 'nullable|string|max:255',
        ]);

        Jadwal::create($validated);

        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil ditambahkan!');
    }

    public function edit(Jadwal $jadwal)
    {
        $kelas = Kelas::select('id', 'nama_kelas', 'tingkat')
            ->orderBy('nama_kelas', 'asc')
            ->get();
        $gurus = Guru::select('id', 'nama', 'nip', 'mata_pelajaran')
            ->orderBy('nama', 'asc')
            ->get();
        return view('jadwal.edit', compact('jadwal', 'kelas', 'gurus'));
    }

    public function update(Request $request, Jadwal $jadwal)
    {
        $validated = $request->validate([
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:guru,id',
            'mata_pelajaran' => 'required|string|max:255',
            'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_mulai' => 'required',
            'jam_selesai' => 'required',
            'ruangan' => 'nullable|string|max:255',
        ]);

        $jadwal->update($validated);

        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil diupdate!');
    }

    public function destroy(Jadwal $jadwal)
    {
        $jadwal->delete();
        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil dihapus!');
    }

    // API Methods
    public function apiIndex()
    {
        $jadwals = Jadwal::with([
            'kelas:id,nama_kelas',
            'guru:id,nama,nip'
        ])
            ->select('id', 'kelas_id', 'guru_id', 'mata_pelajaran', 'hari', 'jam_mulai', 'jam_selesai', 'ruangan')
            ->orderBy('hari', 'asc')
            ->orderBy('jam_mulai', 'asc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $jadwals
        ]);
    }

    public function apiStore(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'kelas' => 'nullable|string|max:255',
            'kelas_id' => 'nullable|integer',
            'guru' => 'nullable|string|max:255',
            'guru_id' => 'nullable|integer',
            'mata_pelajaran' => 'required|string|max:255',
            'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_ke' => 'nullable|string',
            'jam_mulai' => 'nullable|string',
            'jam_selesai' => 'nullable|string',
            'ruangan' => 'nullable|string|max:255',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        try {
            // Cari atau buat kelas
            $kelasId = $request->kelas_id;
            if (!$kelasId && $request->kelas) {
                $kelas = \App\Models\Kelas::where('nama_kelas', $request->kelas)->first();
                if ($kelas) {
                    $kelasId = $kelas->id;
                }
            }
            // Default ke kelas pertama jika tidak ada
            if (!$kelasId) {
                $kelas = \App\Models\Kelas::first();
                $kelasId = $kelas ? $kelas->id : null;
            }

            // Cari atau buat guru
            $guruId = $request->guru_id;
            if (!$guruId && $request->guru) {
                $guru = \App\Models\Guru::where('nama', $request->guru)->first();
                if ($guru) {
                    $guruId = $guru->id;
                }
            }
            // Default ke guru pertama jika tidak ada
            if (!$guruId) {
                $guru = \App\Models\Guru::first();
                $guruId = $guru ? $guru->id : null;
            }

            // Buat jadwal
            $jadwal = Jadwal::create([
                'kelas_id' => $kelasId,
                'guru_id' => $guruId,
                'mata_pelajaran' => $request->mata_pelajaran,
                'hari' => $request->hari,
                'jam_mulai' => $request->jam_mulai ?? $request->jam_ke ?? '07:00',
                'jam_selesai' => $request->jam_selesai ?? $request->jam_ke ?? '08:00',
                'ruangan' => $request->ruangan,
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil ditambahkan',
                'data' => $jadwal->load(['kelas', 'guru'])
            ], 201);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal menambahkan jadwal: ' . $e->getMessage()
            ], 500);
        }
    }

    public function apiShow($id)
    {
        $jadwal = Jadwal::with(['kelas', 'guru'])->find($id);

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $jadwal
        ]);
    }

    public function apiUpdate(Request $request, $id)
    {
        $jadwal = Jadwal::find($id);

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan'
            ], 404);
        }

        $validator = Validator::make($request->all(), [
            'mata_pelajaran' => 'sometimes|required|string|max:255',
            'hari' => 'sometimes|required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_ke' => 'sometimes|required|string',
            'ruangan' => 'nullable|string|max:255',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $jadwal->update($request->all());

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diupdate',
            'data' => $jadwal
        ]);
    }

    public function apiDestroy($id)
    {
        $jadwal = Jadwal::find($id);

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan'
            ], 404);
        }

        $jadwal->delete();

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil dihapus'
        ]);
    }
}
