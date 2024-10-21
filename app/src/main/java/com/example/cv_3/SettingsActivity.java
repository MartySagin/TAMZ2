package com.example.cv_3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.ColorPickerDialog;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private Button depositColorButton, interestColorButton, saveSettingsButton;
    private int depositColor, interestColor;
    private BarChart settingsBarChart;

    private static final String PREFS_NAME = "AppSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        depositColorButton = findViewById(R.id.depositColorButton);
        interestColorButton = findViewById(R.id.interestColorButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        settingsBarChart = findViewById(R.id.settingsBarChart);

        // Načti uložené barvy
        loadSettings();

        // Nastavení výchozího grafu
        setupBarChart(depositColor, interestColor);

        // Výběr barvy pro vklad
        depositColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker(depositColor, new ColorPickerCallback() {
                    @Override
                    public void onColorSelected(int color) {
                        depositColor = color;
                        depositColorButton.setBackgroundColor(depositColor);
                        // Aktualizuj BarChart s novou barvou vkladu
                        setupBarChart(depositColor, interestColor);
                    }
                });
            }
        });

        // Výběr barvy pro úroky
        interestColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker(interestColor, new ColorPickerCallback() {
                    @Override
                    public void onColorSelected(int color) {
                        interestColor = color;
                        interestColorButton.setBackgroundColor(interestColor);
                        // Aktualizuj BarChart s novou barvou úroků
                        setupBarChart(depositColor, interestColor);
                    }
                });
            }
        });

        // Uložit nastavení barev
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    // Nastavení BarChart
    private void setupBarChart(int depositColor, int interestColor) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 100000));  // Ukázkový vklad
        barEntries.add(new BarEntry(1, 20000));   // Ukázkový úrok

        BarDataSet barDataSet = new BarDataSet(barEntries, "Vklad/Úroky");
        barDataSet.setColors(new int[]{depositColor, interestColor});

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);

        settingsBarChart.setData(barData);
        settingsBarChart.getDescription().setEnabled(false);

        settingsBarChart.getAxisLeft().setAxisMinimum(0);
        settingsBarChart.getAxisRight().setAxisMinimum(0);

        settingsBarChart.getAxisLeft().setDrawGridLines(false);
        settingsBarChart.getAxisRight().setDrawGridLines(false);
        settingsBarChart.getXAxis().setDrawGridLines(false);

        barDataSet.setValueTextSize(16f);

        settingsBarChart.getAxisLeft().setAxisMinimum(0);
        settingsBarChart.getAxisRight().setAxisMinimum(0);
        settingsBarChart.setExtraOffsets(0, 0, 0, 5);

        barDataSet.setDrawValues(true);
        settingsBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        settingsBarChart.getDescription().setEnabled(false);

        settingsBarChart.invalidate(); // Refresh grafu
    }

    // Načíst nastavení
    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        depositColor = settings.getInt("depositColor", Color.RED); // Výchozí barva
        interestColor = settings.getInt("interestColor", Color.BLUE); // Výchozí barva

        depositColorButton.setBackgroundColor(depositColor);
        interestColorButton.setBackgroundColor(interestColor);
    }

    // Uložit nastavení
    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        editor.putInt("depositColor", depositColor);
        editor.putInt("interestColor", interestColor);
        editor.apply();

        Toast.makeText(this, "Nastavení uloženo", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK, new Intent()); // Nastaví výsledek pro MainActivity
        finish(); // Ukonči aktivitu
    }

    // Otevřít Color Picker
    private void openColorPicker(int initialColor, final ColorPickerCallback callback) {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this)
                .setTitle("Vyber barvu")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Vybrat", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        callback.onColorSelected(envelope.getColor());
                    }
                })
                .setNegativeButton("Zrušit", (dialogInterface, i) -> dialogInterface.dismiss());

        builder.getColorPickerView().setInitialColor(initialColor);
        builder.show();
    }

    // Callback pro zvolení barvy
    interface ColorPickerCallback {
        void onColorSelected(int color);
    }
}
