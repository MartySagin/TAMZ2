package com.example.cv_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartTypeActivity extends AppCompatActivity {

    public static final String EXTRA_CHART_TYPE = "com.example.cv_3.EXTRA_CHART_TYPE";
    public static final int CHART_TYPE_BAR = 1;
    public static final int CHART_TYPE_PIE = 2;

    private BarChart previewBarChart;
    private PieChart previewPieChart;

    private int depositColor, interestColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_type);

        previewBarChart = findViewById(R.id.previewBarChart);
        previewPieChart = findViewById(R.id.previewPieChart);
        View invisiblePieChartOverlay = findViewById(R.id.invisiblePieChartOverlay);

        int deposit = getIntent().getIntExtra("DEPOSIT", 100000);
        float interestEarned = getIntent().getFloatExtra("INTEREST_EARNED", 20000);

        depositColor = getIntent().getIntExtra("DEPOSIT_COLOR", 0xFF0000FF);
        interestColor = getIntent().getIntExtra("INTEREST_COLOR", 0xFF00FF00);

        setupBarChart(deposit, interestEarned, depositColor, interestColor);

        setupPieChart(deposit, interestEarned, depositColor, interestColor);



        findViewById(R.id.previewBarChart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnChartType(CHART_TYPE_BAR);
            }
        });

        invisiblePieChartOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnChartType(CHART_TYPE_PIE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // This closes the activity and returns to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBarChart(int deposit, float interestEarned, int depositColor, int interestColor) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, deposit));
        barEntries.add(new BarEntry(1, interestEarned));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Vklad/Úroky");
        barDataSet.setColors(depositColor, interestColor);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);
        previewBarChart.setData(barData);

        previewBarChart.getAxisLeft().setAxisMinimum(0);
        previewBarChart.getAxisRight().setAxisMinimum(0);

        previewBarChart.getAxisLeft().setDrawGridLines(false);
        previewBarChart.getAxisRight().setDrawGridLines(false);
        previewBarChart.getXAxis().setDrawGridLines(false);

        barDataSet.setValueTextSize(16f);

        previewBarChart.getAxisLeft().setAxisMinimum(0);
        previewBarChart.getAxisRight().setAxisMinimum(0);
        previewBarChart.setExtraOffsets(0, 0, 0, 5);

        barDataSet.setDrawValues(true);
        previewBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        previewBarChart.getDescription().setEnabled(false);

        previewBarChart.invalidate(); // Refresh
    }

    private void setupPieChart(int deposit, float interestEarned, int depositColor, int interestColor) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(deposit, "Vklad"));
        pieEntries.add(new PieEntry(interestEarned, "Úroky"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Vklad/Úroky");
        pieDataSet.setColors(depositColor, interestColor);

        PieData pieData = new PieData(pieDataSet);
        previewPieChart.setData(pieData);

        pieDataSet.setValueTextSize(16f);
        pieDataSet.setDrawValues(true);

        previewBarChart.getDescription().setEnabled(false);

        previewPieChart.invalidate(); // Refresh
    }

    private void returnChartType(int chartType) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra(EXTRA_CHART_TYPE, chartType);

        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
