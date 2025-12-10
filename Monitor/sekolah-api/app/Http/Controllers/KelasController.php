<?php

namespace App\Http\Controllers;

use App\Models\Kelas;
use App\Models\Guru;
use Illuminate\Http\Request;

class KelasController extends Controller
{
    public function index()
    {
        $kelas = Kelas::with('waliKelas:id,nama')
            ->select('id', 'nama_kelas', 'tingkat', 'jurusan', 'wali_kelas_id', 'ruangan', 'kapasitas')
            ->orderBy('tingkat', 'asc')
            ->paginate(25);
        return view('kelas.index', compact('kelas'));
    }

    public function create()
    {
        $gurus = Guru::select('id', 'nama', 'nip')
            ->orderBy('nama', 'asc')
            ->get();
        return view('kelas.create', compact('gurus'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'nama_kelas' => 'required|string|max:255',
            'tingkat' => 'required|string|max:10',
            'jurusan' => 'nullable|string|max:255',
            'wali_kelas_id' => 'nullable|exists:guru,id',
            'ruangan' => 'nullable|string|max:255',
            'kapasitas' => 'required|integer|min:1',
        ]);

        Kelas::create($validated);

        return redirect()->route('kelas.index')->with('success', 'Data kelas berhasil ditambahkan!');
    }

    public function edit(Kelas $kela)
    {
        $gurus = Guru::select('id', 'nama', 'nip')
            ->orderBy('nama', 'asc')
            ->get();
        return view('kelas.edit', ['kelas' => $kela, 'gurus' => $gurus]);
    }

    public function update(Request $request, Kelas $kela)
    {
        $validated = $request->validate([
            'nama_kelas' => 'required|string|max:255',
            'tingkat' => 'required|string|max:10',
            'jurusan' => 'nullable|string|max:255',
            'wali_kelas_id' => 'nullable|exists:guru,id',
            'ruangan' => 'nullable|string|max:255',
            'kapasitas' => 'required|integer|min:1',
        ]);

        $kela->update($validated);

        return redirect()->route('kelas.index')->with('success', 'Data kelas berhasil diupdate!');
    }

    public function destroy(Kelas $kela)
    {
        $kela->delete();
        return redirect()->route('kelas.index')->with('success', 'Data kelas berhasil dihapus!');
    }
}
