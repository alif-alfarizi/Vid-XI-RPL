<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Validator;
use App\Models\User;

class AuthController extends Controller
{
    /**
     * Show login form (Web)
     */
    public function showLogin()
    {
        if (Auth::check()) {
            return redirect()->route('dashboard');
        }

        return view('auth.login');
    }

    /**
     * Handle login request (Web & API)
     */
    public function login(Request $request)
    {
        // Jika request JSON atau dari API, gunakan apiLogin
        if ($request->expectsJson() || $request->is('api/*') || $request->wantsJson()) {
            return $this->apiLogin($request);
        }

        // Web login - simple validation
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        // Attempt login
        if (Auth::attempt($request->only('email', 'password'), $request->boolean('remember'))) {
            $request->session()->regenerate();

            // Simple admin check
            if (Auth::user()->role !== 'admin') {
                Auth::logout();
                return back()->withErrors(['email' => 'You must be an admin to access this system.']);
            }

            return redirect()->intended('/dashboard');
        }

        return back()->withErrors(['email' => 'Invalid credentials.'])->withInput($request->only('email'));
    }

    /**
     * API Login - khusus untuk request dari Android/API
     */
    public function apiLogin(Request $request)
    {
        // Log untuk debugging
        Log::info('API Login attempt', [
            'email' => $request->email,
            'has_password' => !empty($request->password),
            'content_type' => $request->header('Content-Type'),
            'all_input' => $request->all()
        ]);

        // Validasi - email tidak harus format email (bisa NIP)
        $validator = Validator::make($request->all(), [
            'email' => 'required|string',
            'password' => 'required|string',
        ]);

        if ($validator->fails()) {
            Log::warning('API Login validation failed', ['errors' => $validator->errors()]);
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        // Cari user berdasarkan email
        $user = User::where('email', $request->email)->first();

        if (!$user) {
            Log::warning('API Login user not found', ['email' => $request->email]);
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan'
            ], 401);
        }

        if (!Hash::check($request->password, $user->password)) {
            Log::warning('API Login password mismatch', ['email' => $request->email]);
            return response()->json([
                'success' => false,
                'message' => 'Password salah'
            ], 401);
        }

        // Generate token
        $token = $user->createToken('auth-token')->plainTextToken;

        Log::info('API Login successful', ['user_id' => $user->id, 'email' => $user->email]);

        return response()->json([
            'success' => true,
            'message' => 'Login berhasil',
            'data' => [
                'user' => $user,
                'token' => $token
            ]
        ]);
    }

    /**
     * Register user baru (API)
     */
    public function register(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'nama' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:6',
            'role' => 'required|string|in:Siswa,Kurikulum,Kepala Sekolah,Admin'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = User::create([
            'nama' => $request->nama,
            'name' => $request->nama,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'role' => $request->role
        ]);

        $token = $user->createToken('auth-token')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Registrasi berhasil',
            'data' => [
                'user' => $user,
                'token' => $token
            ]
        ], 201);
    }

    /**
     * Handle logout request
     */
    public function logout(Request $request)
    {
        // Jika request dari API
        if ($request->expectsJson() || $request->is('api/*')) {
            if ($request->user()) {
                $request->user()->currentAccessToken()->delete();
            }

            return response()->json([
                'success' => true,
                'message' => 'Logout berhasil'
            ]);
        }

        // Web logout
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('login');
    }
}
