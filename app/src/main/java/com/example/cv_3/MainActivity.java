package com.example.cv_3;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SeekBar depositSeekBar, interestSeekBar, periodSeekBar;
    private TextView depositValueTextView, interestValueTextView, periodValueTextView;
    private TextView sumTextView, interestTextView;
    private BarChart barChart;

    private final int DEPOSIT_STEP = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        depositSeekBar = findViewById(R.id.depositSeekBar);
        interestSeekBar = findViewById(R.id.interestSeekBar);
        periodSeekBar = findViewById(R.id.periodSeekBar);
        barChart = findViewById(R.id.barChart);

        depositValueTextView = findViewById(R.id.depositValueTextView);
        interestValueTextView = findViewById(R.id.interestValueTextView);
        periodValueTextView = findViewById(R.id.periodValueTextView);
        sumTextView = findViewById(R.id.sumTextView);
        interestTextView = findViewById(R.id.interestTextView);

        depositSeekBar.setMax(100);
        depositSeekBar.setProgress(10);

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

        updateChart(deposit, (float) interestEarned);
    }

    private void updateChart(int deposit, float interestEarned) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(0, deposit));
        barEntries.add(new BarEntry(1, interestEarned));


        BarDataSet barDataSet = new BarDataSet(barEntries, "Vklad/Úroky");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


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
}
