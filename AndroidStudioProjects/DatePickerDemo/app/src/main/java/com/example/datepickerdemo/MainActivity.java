package com.example.datepickerdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
    }

    // Inisialisasi komponen
    public void load() {
        etTanggal = findViewById(R.id.etTanggal);
    }

    // Metode yang dipanggil saat EditText diklik
    public void showDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Tambah 1 ke bulan karena dimulai dari 0
                        int month = selectedMonth + 1;
                        // Format tanggal: DD/MM/YYYY
                        String tanggal = String.format("%02d/%02d/%04d", selectedDay, month, selectedYear);
                        etTanggal.setText(tanggal);
                    }
                },
                tahun, bulan, hari
        );

        datePickerDialog.show();
    }
}
