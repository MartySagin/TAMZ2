package com.example.cv_3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SeekBar depositSeekBar, interestSeekBar, periodSeekBar, monthlyDepositSeekBar;
    private TextView depositValueTextView, interestValueTextView, periodValueTextView, monthlyDepositValueTextView;
    private TextView sumTextView, interestTextView;
    private BarChart barChart;
    private PieChart pieChart;
    private Button saveButton;

    private ArrayList<String> historyList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    private final int DEPOSIT_STEP = 10000;

    private int depositColor, interestColor;

    private static final String PREFS_NAME = "AppSettings";
    private static final String HISTORY_KEY = "historyList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        depositSeekBar = findViewById(R.id.depositSeekBar);
        interestSeekBar = findViewById(R.id.interestSeekBar);
        periodSeekBar = findViewById(R.id.periodSeekBar);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        depositValueTextView = findViewById(R.id.depositValueTextView);
        interestValueTextView = findViewById(R.id.interestValueTextView);
        periodValueTextView = findViewById(R.id.periodValueTextView);

        sumTextView = findViewById(R.id.sumTextView);
        interestTextView = findViewById(R.id.interestTextView);

        depositSeekBar.setMax(100);
        depositSeekBar.setProgress(10);

        saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadHistory();
        loadGraphColors();
        updateValues();

        depositSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int deposit = progress * DEPOSIT_STEP;

                depositValueTextView.setText(deposit + " Kč");
                updateValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        interestSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interestValueTextView.setText(progress + " %");
                updateValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        periodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                periodValueTextView.setText(progress + " roků");
                updateValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCalculationToHistory();
            }
        });
    }

    private void loadGraphColors() {
        SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);

        depositColor = settings.getInt("depositColor", Color.RED);
        interestColor = settings.getInt("interestColor", Color.BLUE);
    }

    private void saveCalculationToHistory() {
        loadHistory();

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Prague");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(timeZone);
        String currentTime = dateFormat.format(new Date());

        String sum = sumTextView.getText().toString();
        String interest = interestTextView.getText().toString();

        String historyEntry = "Datum: " + currentTime + "\n" + sum + "\n" + interest + "\n";
        historyList.add(historyEntry);

        saveHistory();

        Toast.makeText(this, "Uloženo", Toast.LENGTH_SHORT).show();
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> historySet = new HashSet<>(historyList);
        editor.putStringSet(HISTORY_KEY, historySet);
        editor.apply();
    }

    private void loadHistory() {
        Set<String> historySet = sharedPreferences.getStringSet(HISTORY_KEY, new HashSet<>());
        historyList.clear();
        historyList.addAll(historySet);
    }

    public void clearHistory() {
        historyList.clear();
        saveHistory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.chartTypeMenu) {
            Intent intent = new Intent(MainActivity.this, ChartTypeActivity.class);

            int deposit = depositSeekBar.getProgress() * DEPOSIT_STEP;
            double interestRateDecimal = interestSeekBar.getProgress() / 100.0;
            double finalAmount = deposit * Math.pow((1 + interestRateDecimal), periodSeekBar.getProgress());
            float interestEarned = (float) (finalAmount - deposit);

            intent.putExtra("DEPOSIT", deposit);
            intent.putExtra("INTEREST_EARNED", interestEarned);

            intent.putExtra("DEPOSIT_COLOR", depositColor);
            intent.putExtra("INTEREST_COLOR", interestColor);

            startActivityForResult(intent, 1);

            return true;
        }else if (id == R.id.historyMenu) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putStringArrayListExtra("HISTORY_LIST", historyList);

            startActivity(intent);

            return true;
        }else if (id == R.id.settingsMenu) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 2);

            onActivityResult(2, RESULT_OK, intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                int chartType = data.getIntExtra(ChartTypeActivity.EXTRA_CHART_TYPE, ChartTypeActivity.CHART_TYPE_BAR);
                if (chartType == ChartTypeActivity.CHART_TYPE_BAR) {
                    barChart.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.GONE);

                    updateChart(depositSeekBar.getProgress() * DEPOSIT_STEP, (float) calculateInterest());
                } else if (chartType == ChartTypeActivity.CHART_TYPE_PIE) {
                    pieChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);

                    updatePieChart(depositSeekBar.getProgress() * DEPOSIT_STEP, (float) calculateInterest());
                }
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {
                int newDepositColor = data.getIntExtra("depositColor", Color.RED);
                int newInterestColor = data.getIntExtra("interestColor", Color.BLUE);

                depositColor = newDepositColor;
                interestColor = newInterestColor;

                loadGraphColors();
                updateValues();
            }
        }
    }

    private void updateValues() {
        int deposit = depositSeekBar.getProgress() * DEPOSIT_STEP;
        int interestRate = interestSeekBar.getProgress();
        int period = periodSeekBar.getProgress();

        double interestRateDecimal = interestRate / 100.0;
        double finalAmount = deposit * Math.pow((1 + interestRateDecimal), period);
        double interestEarned = finalAmount - deposit;

        sumTextView.setText("Naspořená suma: " + String.format("%,.0f Kč", finalAmount));
        interestTextView.setText("Z toho úroky: " + String.format("%,.0f Kč", interestEarned));

        if (barChart.getVisibility() == View.VISIBLE)
            updateChart(deposit, (float) interestEarned);
        else
            updatePieChart(deposit, (float) interestEarned);
    }

    private double calculateInterest() {
        int deposit = depositSeekBar.getProgress() * DEPOSIT_STEP;
        double interestRateDecimal = interestSeekBar.getProgress() / 100.0;
        double finalAmount = deposit * Math.pow((1 + interestRateDecimal), periodSeekBar.getProgress());
        return finalAmount - deposit;
    }

    private void updateChart(int deposit, float interestEarned) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, deposit));
        barEntries.add(new BarEntry(1, interestEarned));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Vklad/Úroky");
        barDataSet.setColors(depositColor, interestColor);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f", value);
            }
        });

        barChart.setData(barData);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        barDataSet.setValueTextSize(16f);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMinimum(0);
        barChart.setExtraOffsets(0, 0, 0, 5);

        barDataSet.setDrawValues(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setEnabled(false);


        barChart.invalidate();
    }

    private void updatePieChart(int deposit, float interestEarned) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(deposit, "Vklad"));
        pieEntries.add(new PieEntry(interestEarned, "Úroky"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Vklad/Úroky");
        pieDataSet.setColors(depositColor, interestColor);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);

        pieChart.invalidate();
    }
}
