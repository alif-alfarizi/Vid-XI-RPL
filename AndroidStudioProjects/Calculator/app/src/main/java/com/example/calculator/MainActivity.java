package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Deklarasi variabel untuk komponen UI
    private EditText input1, input2;
    private Button buttonAdd, buttonSubtract, buttonMultiply, buttonDivide, buttonClear;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen UI menggunakan findViewById
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        result = findViewById(R.id.result);
        
        // Inisialisasi tombol-tombol operasi
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSubtract = findViewById(R.id.buttonSubtract);
        buttonMultiply = findViewById(R.id.buttonMultiply);
        buttonDivide = findViewById(R.id.buttonDivide);
        buttonClear = findViewById(R.id.buttonClear);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tombol Penjumlahan
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('+');
            }
        });

        // Tombol Pengurangan
        buttonSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('-');
            }
        });

        // Tombol Perkalian
        buttonMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('×');
            }
        });

        // Tombol Pembagian
        buttonDivide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate('/');
            }
        });

        // Tombol Clear
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengosongkan input dan hasil
                input1.setText("");
                input2.setText("");
                result.setText("0");
                // Fokus ke input pertama
                input1.requestFocus();
            }
        });
    }

    // Metode untuk melakukan perhitungan
    private void calculate(char operator) {
        // Ambil input dari EditText
        String strNum1 = input1.getText().toString().trim();
        String strNum2 = input2.getText().toString().trim();

        // Validasi input kosong
        if (strNum1.isEmpty() || strNum2.isEmpty()) {
            Toast.makeText(this, "Mohon masukkan kedua angka terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Konversi string ke double
            double num1 = Double.parseDouble(strNum1);
            double num2 = Double.parseDouble(strNum2);
            double hasil = 0;

            // Lakukan operasi sesuai operator
            switch (operator) {
                case '+':
                    hasil = num1 + num2;
                    break;
                case '-':
                    hasil = num1 - num2;
                    break;
                case '×':
                    hasil = num1 * num2;
                    break;
                case '/':
                    // Cek pembagian dengan nol
                    if (num2 == 0) {
                        Toast.makeText(this, "Tidak dapat membagi dengan nol!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hasil = num1 / num2;
                    break;
            }

            // Format hasil agar tidak menampilkan desimal jika hasilnya bilangan bulat
            String hasilStr;
            if (hasil == (long) hasil) {
                hasilStr = String.format("%d", (long) hasil);
            } else {
                hasilStr = String.format("%.2f", hasil);
            }

            // Tampilkan hasil
            result.setText(hasilStr);

        } catch (NumberFormatException e) {
            // Tangani jika input bukan angka yang valid
            Toast.makeText(this, "Mohon masukkan angka yang valid!", Toast.LENGTH_SHORT).show();
        }
    }
}