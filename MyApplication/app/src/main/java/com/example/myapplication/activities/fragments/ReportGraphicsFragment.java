package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.AdapterReport;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataReport;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ReportGraphicsFragment extends Fragment {

    // Views
    private PieChart pieChart_profits;
    private PieChart pieChart_spends;
    private LinearLayout linearLayout_data;
    private TextView textView_info;

    // Database manager instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_graphics, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // Set components
        pieChart_profits = view.findViewById(R.id.piechart_reportGraphicsFragment_profitchart);
        pieChart_spends = view.findViewById(R.id.piechart_reportGraphicsFragment_spendchart);
        textView_info = view.findViewById(R.id.textView_reportGraphicsFragment_info);
        linearLayout_data = view.findViewById(R.id.layout_reportGraphicsFragment_data);

        // Set charts options
        Description descriptionProfit = pieChart_profits.getDescription();
        descriptionProfit.setText(getString(R.string.reportGraphicsFragment_profitDescription));
        pieChart_profits.setDescription(descriptionProfit);
        pieChart_profits.setRotationEnabled(true);
        pieChart_profits.setHoleRadius(40f);
        pieChart_profits.setTransparentCircleAlpha(120);
        pieChart_profits.setCenterText(getString(R.string.reportGraphicsFragment_profitTitle));
        pieChart_profits.setCenterTextSize(12);
        pieChart_profits.setDrawEntryLabels(false);

        Description descriptionSpend = pieChart_spends.getDescription();
        descriptionSpend.setText(getString(R.string.reportGraphicsFragment_spendDescription));
        pieChart_spends.setDescription(descriptionSpend);
        pieChart_spends.setRotationEnabled(true);
        pieChart_spends.setHoleRadius(40f);
        pieChart_spends.setTransparentCircleAlpha(120);
        pieChart_spends.setCenterText(getString(R.string.reportGraphicsFragment_spendTitle));
        pieChart_spends.setCenterTextSize(12);
        pieChart_spends.setDrawEntryLabels(false);

        // If there is default period previously selected, set that period
        // Get the shared preferences period of instance
        SharedPreferences prefsReportPeriod = Objects.requireNonNull(getActivity()).getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        String reportPeriod = prefsReportPeriod.getString(MainActivity.idInstance, null);

        if (reportPeriod != null) {
            // Hide info textView
            textView_info.setVisibility(View.GONE);

            // Get the default period.
            String begPeriodDate = reportPeriod.substring(0, 10);
            String finPeriodDate = reportPeriod.substring(11, 21);
            showProfitsReport(begPeriodDate, finPeriodDate);

            // Show Recycler view
            linearLayout_data.setVisibility(View.VISIBLE);
        } else {
            // Show info textView and hide recycler view
            textView_info.setVisibility(View.VISIBLE);
            linearLayout_data.setVisibility(View.GONE);
        }

        // Chart value click listener
        pieChart_profits.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //TODO Click listener charts
            }

            @Override
            public void onNothingSelected() {
            }
        });
        pieChart_spends.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //TODO Click listener charts
            }

            @Override
            public void onNothingSelected() {
            }
        });

        return view;
    }

    private void showProfitsReport(String begDate, String finDate) {
        // Get Entries from database with period and instance as filter
        Cursor profits = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finDate, 0);
        // Get Entries from database with period and instance as filter
        Cursor spends = db.getEntrySpendInDate(MainActivity.idInstance, begDate, finDate, 0);

        // Get categories from database with instance as filter
        Cursor categories = db.getCategoriesByInstance(MainActivity.idInstance);

        // Process categories
        ArrayList<String> categoriesNames = new ArrayList<>();
        ArrayList<PieEntry> percentagesProfitsList = new ArrayList<>();
        ArrayList<PieEntry> percentagesSpendsList = new ArrayList<>();
        ArrayList<Float> categoriesTotalAmountProfits = new ArrayList<>();
        ArrayList<Float> categoriesTotalAmountSpends = new ArrayList<>();
        while (categories.moveToNext()) {
            float totalAmountProfits = 0;
            float totalAmountSpends = 0;
            String categId = categories.getString(0);
            while (profits.moveToNext()) {
                String profitCategId = profits.getString(2);
                if (profitCategId.equals(categId)) {
                    float amount = profits.getFloat(5);
                    totalAmountProfits += amount;
                }
            }
            while (spends.moveToNext()) {
                String spendCategId = spends.getString(2);
                if (spendCategId.equals(categId)) {
                    float amount = spends.getFloat(6);
                    totalAmountSpends += amount;
                }
            }

            // Add data in temporal lists
            categoriesNames.add(categories.getString(1));
            categoriesTotalAmountProfits.add(totalAmountProfits);
            categoriesTotalAmountSpends.add(totalAmountSpends);

            // Reset profits and spends Cursor position
            profits.moveToPosition(-1);
            spends.moveToPosition(-1);
        }
        categories.moveToPosition(-1);

        // Get total amount to calculate percentage
        float totalProfits = 0;
        float totalSpends = 0;
        for (int i = 0; i < categoriesTotalAmountProfits.size(); i++) {
            totalProfits += categoriesTotalAmountProfits.get(i);
        }
        for (int i = 0; i < categoriesTotalAmountSpends.size(); i++) {
            totalSpends += categoriesTotalAmountSpends.get(i);
        }
        // Calculate percentage for each category
        for (int i = 0; i < categoriesNames.size(); i++) {
            assert categoriesNames != null;
            // Calculate percentage
            float totalAmountCategoryProfits = categoriesTotalAmountProfits.get(i);
            float totalAmountCategorySpends = categoriesTotalAmountSpends.get(i);
            float percentageProfits = (totalAmountCategoryProfits * 100) / (totalProfits);
            float percentageSpends = (totalAmountCategorySpends * 100) / (totalSpends);

            percentagesProfitsList.add(new PieEntry(percentageProfits, categoriesNames.get(i)));
            percentagesSpendsList.add(new PieEntry(percentageSpends, categoriesNames.get(i)));
        }

        // Create data set
        PieDataSet pieDataSetProfit = new PieDataSet(percentagesProfitsList, getString(R.string.reportGraphicsFragment_profitTitle));
        pieDataSetProfit.setSliceSpace(2);
        pieDataSetProfit.setValueTextSize(10);

        PieDataSet pieDataSetSpend = new PieDataSet(percentagesSpendsList, getString(R.string.reportGraphicsFragment_spendTitle));
        pieDataSetSpend.setSliceSpace(2);
        pieDataSetSpend.setValueTextSize(10);

        // Add colors
        ArrayList<Integer> chartColors = new ArrayList<>();
        chartColors.add(Color.CYAN);
        chartColors.add(Color.LTGRAY);
        chartColors.add(Color.GREEN);
        chartColors.add(Color.GRAY);
        chartColors.add(Color.MAGENTA);
        chartColors.add(Color.YELLOW);
        chartColors.add(Color.RED);
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorOrangeDark));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorPink));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorPrimary));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorWhiteMint));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorBlueDark));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorPrimaryDark));
        chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorAccent));
        chartColors.add(Color.DKGRAY);
        pieDataSetProfit.setColors(chartColors);
        pieDataSetSpend.setColors(chartColors);

        // Add legend
        Legend legendProfit = pieChart_profits.getLegend();
        legendProfit.setForm(Legend.LegendForm.CIRCLE);
        legendProfit.setOrientation(Legend.LegendOrientation.VERTICAL);
        legendProfit.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legendProfit.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        Legend legendSpend = pieChart_spends.getLegend();
        legendSpend.setForm(Legend.LegendForm.CIRCLE);
        legendSpend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legendSpend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legendSpend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Create pie data object
        PieData pieDataProfit = new PieData(pieDataSetProfit);
        pieChart_profits.setData(pieDataProfit);
        pieChart_profits.invalidate();

        PieData pieDataSpend = new PieData(pieDataSetSpend);
        pieChart_spends.setData(pieDataSpend);
        pieChart_spends.invalidate();
    }

}
