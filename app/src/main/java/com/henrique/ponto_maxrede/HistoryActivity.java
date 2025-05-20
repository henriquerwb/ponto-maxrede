package com.henrique.ponto_maxrede;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private TextView tvHistorico;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Histórico de Pontos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvHistorico = findViewById(R.id.tvHistorico);
        calendarView = findViewById(R.id.calendarView);

        mostrarRegistrosDoDia(calendarView.getDate());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            mostrarRegistrosDoDia(dayOfMonth, month + 1, year);
        });
    }

    private void mostrarRegistrosDoDia(int dia, int mes, int ano) {
        String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%02d", dia, mes, ano % 100);
        mostrarRegistrosDoDia(dataFormatada);
    }

    private void mostrarRegistrosDoDia(long millis) {
        Date data = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        mostrarRegistrosDoDia(sdf.format(data));
    }

    private void mostrarRegistrosDoDia(String dataDesejada) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(getFilesDir(), "registro_ponto.txt");

            if (!file.exists()) {
                tvHistorico.setText("Nenhum registro encontrado.");
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String linha;
            boolean encontrou = false;
            while ((linha = br.readLine()) != null) {
                if (linha.startsWith(dataDesejada)) {
                    sb.append(linha).append("\n");
                    encontrou = true;
                }
            }

            br.close();

            if (encontrou) {
                tvHistorico.setText(sb.toString());
            } else {
                tvHistorico.setText("Nenhum registro neste dia.");
            }

        } catch (Exception e) {
            tvHistorico.setText("Erro ao carregar histórico.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
