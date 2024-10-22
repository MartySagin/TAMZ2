package com.example.cv_3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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

        Button clearMemoryButton = findViewById(R.id.clearMemoryButton);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        clearMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                depositColor = sharedPreferences.getInt("depositColor", Color.RED);
                interestColor = sharedPreferences.getInt("interestColor", Color.BLUE);

                setupBarChart(depositColor, interestColor);

                depositColorButton.setBackgroundColor(depositColor);
                interestColorButton.setBackgroundColor(interestColor);

                Toast.makeText(SettingsActivity.this, "Paměť byla vymazána", Toast.LENGTH_SHORT).show();
            }
        });

        loadSettings();

        setupBarChart(depositColor, interestColor);

        depositColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker(depositColor, new ColorPickerCallback() {
                    @Override
                    public void onColorSelected(int color) {
                        depositColor = color;
                        depositColorButton.setBackgroundColor(depositColor);

                        setupBarChart(depositColor, interestColor);
                    }
                });
            }
        });

        interestColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker(interestColor, new ColorPickerCallback() {
                    @Override
                    public void onColorSelected(int color) {
                        interestColor = color;
                        interestColorButton.setBackgroundColor(interestColor);

                        setupBarChart(depositColor, interestColor);
                    }
                });
            }
        });

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBarChart(int depositColor, int interestColor) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 100000));
        barEntries.add(new BarEntry(1, 20000));

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

        settingsBarChart.invalidate();
    }

    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        depositColor = settings.getInt("depositColor", Color.RED);
        interestColor = settings.getInt("interestColor", Color.BLUE);

        depositColorButton.setBackgroundColor(depositColor);
        interestColorButton.setBackgroundColor(interestColor);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        editor.putInt("depositColor", depositColor);
        editor.putInt("interestColor", interestColor);
        editor.apply();

        Toast.makeText(this, "Nastavení uloženo", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("depositColor", depositColor);
        resultIntent.putExtra("interestColor", interestColor);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

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

    interface ColorPickerCallback {
        void onColorSelected(int color);
    }
}
