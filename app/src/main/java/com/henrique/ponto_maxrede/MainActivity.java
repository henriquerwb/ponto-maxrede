package com.henrique.ponto_maxrede;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnRegistrarPonto, btnVerHistorico;
    private Handler handler = new Handler();
    private boolean isPressed = false;
    private Runnable runnable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegistrarPonto = findViewById(R.id.btnRegistrarPonto);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);

        btnRegistrarPonto.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    if (!isPressed) {
                        isPressed = true;
                        runnable = () -> {
                            registrarPonto();
                            isPressed = false;
                        };
                        handler.postDelayed(runnable, 2000); // 2 segundos
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(runnable);
                    isPressed = false;
                    break;
            }
            return true;
        });

        btnVerHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void registrarPonto() {
        String dataHoje = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
        String arquivo = "registro_ponto.txt";

        try {
            File file = new File(getFilesDir(), arquivo);
            StringBuilder registrosHoje = new StringBuilder();

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (linha.startsWith(dataHoje)) {
                        registrosHoje.append(linha).append("\n");
                    }
                }
                br.close();
            }

            boolean entrada1 = registrosHoje.toString().contains("ENTRADA 1");
            boolean saida1 = registrosHoje.toString().contains("SAÍDA 1");
            boolean entrada2 = registrosHoje.toString().contains("ENTRADA 2");
            boolean saida2 = registrosHoje.toString().contains("SAÍDA 2");

            String pontoParaRegistrar = null;

            if (!entrada1) {
                pontoParaRegistrar = "ENTRADA 1";
            } else if (!saida1) {
                pontoParaRegistrar = "SAÍDA 1";
            } else if (!entrada2) {
                pontoParaRegistrar = "ENTRADA 2";
            } else if (!saida2) {
                pontoParaRegistrar = "SAÍDA 2";
            } else {
                Toast.makeText(this, "Todos os pontos já foram registrados hoje.", Toast.LENGTH_LONG).show();
                return;
            }

            String horario = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            String registro = dataHoje + " - " + pontoParaRegistrar + " - " + horario + "\n";

            FileOutputStream fos = openFileOutput(arquivo, MODE_APPEND);
            fos.write(registro.getBytes());
            fos.close();

            Toast.makeText(this, pontoParaRegistrar + " registrado com sucesso!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao registrar ponto: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
