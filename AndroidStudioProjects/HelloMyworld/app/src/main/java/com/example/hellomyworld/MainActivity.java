package com.example.fullfeaturedcounter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity untuk aplikasi penghitung.
 * Menampilkan nilai penghitung dan menangani interaksi tombol.
 */
public class MainActivity extends AppCompatActivity {

    // Variabel penghitung global
    private int counter = 0;

    // Referensi ke TextView counter
    private TextView tvCounterValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi TextView
        tvCounterValue = findViewById(R.id.tvCounterValue);

        // Set nilai awal counter
        tvCounterValue.setText(String.valueOf(counter));
    }

    /**
     * Dipanggil saat tombol COUNTER UP diklik.
     */
    public void onIncrementClick(View view) {
        counter++;
        tvCounterValue.setText(String.valueOf(counter));
        Log.d("FullCounterApp", "Counter: " + counter);
    }

    /**
     * Dipanggil saat tombol COUNTER DOWN diklik.
     */
    public void onDecrementClick(View view) {
        counter--;
        tvCounterValue.setText(String.valueOf(counter));
        Log.d("FullCounterApp", "Counter: " + counter);
    }
}