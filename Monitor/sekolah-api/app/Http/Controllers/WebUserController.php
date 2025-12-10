<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class WebUserController extends Controller
{
    public function index()
    {
        $users = User::orderBy('created_at', 'desc')->paginate(10);
        return view('users.index', compact('users'));
    }

    public function create()
    {
        $kelas = \App\Models\Kelas::orderBy('nama_kelas')->get();
        return view('users.create', compact('kelas'));
    }

    public function store(Request $request)
    {
        $rules = [
            'nama' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|min:6',
            'role' => 'required|in:admin,siswa,kurikulum,kepala-sekolah',
        ];

        // Tambah validasi kelas_id jika role siswa
        if ($request->role === 'siswa') {
            $rules['kelas_id'] = 'required|exists:kelas,id';
        }

        $validated = $request->validate($rules);

        $validated['password'] = Hash::make($validated['password']);

        User::create($validated);

        return redirect()->route('manage-users.index')->with('success', 'User berhasil ditambahkan!');
    }

    public function edit(User $manage_user)
    {
        $kelas = \App\Models\Kelas::orderBy('nama_kelas')->get();
        return view('users.edit', ['user' => $manage_user, 'kelas' => $kelas]);
    }

    public function update(Request $request, User $manage_user)
    {
        $rules = [
            'nama' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $manage_user->id,
            'password' => 'nullable|min:6',
            'role' => 'required|in:admin,siswa,kurikulum,kepala-sekolah',
        ];

        // Tambah validasi kelas_id jika role siswa
        if ($request->role === 'siswa') {
            $rules['kelas_id'] = 'required|exists:kelas,id';
        } else {
            // Set kelas_id null jika bukan siswa
            $request->merge(['kelas_id' => null]);
        }

        $validated = $request->validate($rules);

        if ($request->filled('password')) {
            $validated['password'] = Hash::make($validated['password']);
        } else {
            unset($validated['password']);
        }

        $manage_user->update($validated);

        return redirect()->route('manage-users.index')->with('success', 'User berhasil diupdate!');
    }

    public function destroy(User $manage_user)
    {
        $manage_user->delete();
        return redirect()->route('manage-users.index')->with('success', 'User berhasil dihapus!');
    }
}
