package com.example.mycounterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
    }

    // Inisialisasi komponen UI
    private void load() {
        tvHasil = findViewById(R.id.tvHasil);
        tvHasil.setText(count + "");
    }

    // Method untuk tombol COUNTER UP
    public void onUp(View view) {
        count++; // tambah nilai counter
        tvHasil.setText(count + ""); // update tampilan
    }

    public void onDown(View view) {
        count--; // kurangi nilai counter
        tvHasil.setText(count + ""); // update tampilan
    }
}