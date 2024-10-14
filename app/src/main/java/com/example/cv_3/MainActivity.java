package com.example.cv_3;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
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

    private final int DEPOSIT_STEP = 10000;  // Step size for deposit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find SeekBars, TextViews, and BarChart
        depositSeekBar = findViewById(R.id.depositSeekBar);
        interestSeekBar = findViewById(R.id.interestSeekBar);
        periodSeekBar = findViewById(R.id.periodSeekBar);
        barChart = findViewById(R.id.barChart);

        depositValueTextView = findViewById(R.id.depositValueTextView);
        interestValueTextView = findViewById(R.id.interestValueTextView);
        periodValueTextView = findViewById(R.id.periodValueTextView);
        sumTextView = findViewById(R.id.sumTextView);
        interestTextView = findViewById(R.id.interestTextView);

        // Adjust max value for deposit SeekBar (1 to 100 for steps of 10,000)
        depositSeekBar.setMax(100);  // Set max value to 100 (for 1,000,000 / 10,000 step size)

        // Set initial values
        updateValues();

        // Set SeekBar change listeners
        depositSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Multiply progress by the step size
                int deposit = progress * DEPOSIT_STEP;
                depositValueTextView.setText(deposit + " Kč");
                updateValues(); // Recalculate and update TextViews
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
                updateValues(); // Recalculate and update TextViews
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
                updateValues(); // Recalculate and update TextViews
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    // Method to calculate and update the values of sum and interest
    private void updateValues() {
        // Get values from SeekBars
        int deposit = depositSeekBar.getProgress() * DEPOSIT_STEP;  // Multiply by step size
        int interestRate = interestSeekBar.getProgress();
        int period = periodSeekBar.getProgress();

        // Calculate compound interest
        double interestRateDecimal = interestRate / 100.0;
        double finalAmount = deposit * Math.pow((1 + interestRateDecimal), period);
        double interestEarned = finalAmount - deposit;

        // Update TextViews with calculated values
        sumTextView.setText("Naspořená suma: " + String.format("%,.2f Kč", finalAmount));
        interestTextView.setText("Z toho úroky: " + String.format("%,.2f Kč", interestEarned));

        // Update BarChart with new data
        updateChart(deposit, (float) interestEarned);
    }

    // Method to update the BarChart based on the values of deposit and interest earned
    private void updateChart(int deposit, float interestEarned) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        // Add entries for the deposit and interest
        barEntries.add(new BarEntry(0, deposit));           // Vklad
        barEntries.add(new BarEntry(1, interestEarned));    // Úrok

        // Create the data set
        BarDataSet barDataSet = new BarDataSet(barEntries, "Vklad/Úroky");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);  // Set colors for the bars

        // Create the BarData object with the data set
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);  // Set custom bar width

        // Set value formatter to display values above bars
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%,.0f", value);  // Format values as whole numbers
            }
        });

        // Configure the chart
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);  // Disable description

        barChart.invalidate();  // Refresh the chart
    }
}
