package com.example.intentactivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BarangActivity extends AppCompatActivity {
    private TextView tvBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        tvBarang = findViewById(R.id.tvBarang);

        String namaBarang = getIntent().getStringExtra("NAMA_BARANG");
        tvBarang.setText(namaBarang != null ? namaBarang : "Tidak ada data");
    }
}