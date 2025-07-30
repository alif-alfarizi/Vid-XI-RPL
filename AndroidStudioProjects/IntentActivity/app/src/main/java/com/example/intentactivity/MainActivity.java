package com.example.intentactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText etBarang;
    private Button btnBarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBarang = findViewById(R.id.etBarang);
        btnBarang = findViewById(R.id.btnBarang);

        btnBarang.setOnClickListener(v -> goToBarangActivity());
    }

    private void goToBarangActivity() {
        String namaBarang = etBarang.getText().toString();
        Intent intent = new Intent(MainActivity.this, BarangActivity.class);
        intent.putExtra("NAMA_BARANG", namaBarang);
        startActivity(intent);
    }
}