package com.example.messegedialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnToast, btnSimpleDialog, btnConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan layout ini ada dan sesuai

        // Inisialisasi tombol dari layout
        btnToast = findViewById(R.id.btnToast);
        btnSimpleDialog = findViewById(R.id.btnSimpleDialog);
        btnConfirmDialog = findViewById(R.id.btnConfirmDialog);

        // Tombol Toast
        btnToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ini adalah pesan Toast!", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol AlertDialog sederhana
        btnSimpleDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Dialog Info");
                builder.setMessage("Ini adalah AlertDialog sederhana.");
                builder.show();
            }
        });

        // Tombol AlertDialog konfirmasi
        btnConfirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah Anda yakin ingin melanjutkan?");

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Anda memilih Ya", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Anda memilih Tidak", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });
    }
}
