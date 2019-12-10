package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;
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
import java.util.Objects;

public class ReportGraphicsFragment extends Fragment {

    // Views
    private PieChart pieChart_profits;
    private PieChart pieChart_spends;
    private ScrollView scrollview_data;
    private ProgressBar progressBar_graphicProgress;
    private TextView textView_info;
    private TextView textView_titleProfit;
    private TextView textView_titleSpend;
    private TextView textView_periodProfit;
    private TextView textView_periodSpend;

    // Global variables
    ArrayList<PieEntry> percentagesProfitsList;
    ArrayList<PieEntry> percentagesSpendsList;

    // Database manager instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_graphics, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());
        Cursor categoriesTmp = db.getCategoriesByInstance(MainActivity.idInstance);

        // Set components
        pieChart_profits = view.findViewById(R.id.piechart_reportGraphicsFragment_profitchart);
        pieChart_spends = view.findViewById(R.id.piechart_reportGraphicsFragment_spendchart);
        textView_info = view.findViewById(R.id.textView_reportGraphicsFragment_info);
        textView_titleProfit = view.findViewById(R.id.textView_reportGraphicsFragment_titleProfit);
        textView_titleSpend = view.findViewById(R.id.textView_reportGraphicsFragment_titleSpend);
        textView_periodProfit = view.findViewById(R.id.textView_reportGraphicsFragment_periodProfit);
        textView_periodSpend = view.findViewById(R.id.textView_reportGraphicsFragment_periodSpend);
        scrollview_data = view.findViewById(R.id.scrollview_reportGraphicsFragment_data);
        progressBar_graphicProgress = view.findViewById(R.id.progressBar_reportGraphicsFragment);

        // Set charts options
        Description descriptionProfit = pieChart_profits.getDescription();
        descriptionProfit.setText(getString(R.string.reportGraphicsFragment_profitDescription));
        pieChart_profits.setDescription(descriptionProfit);
        pieChart_profits.setRotationEnabled(false);
        pieChart_profits.setHoleRadius(40f);
        pieChart_profits.setTransparentCircleAlpha(120);
        pieChart_profits.setCenterText(getString(R.string.reportGraphicsFragment_profitTitle));
        pieChart_profits.setCenterTextSize(12);
        pieChart_profits.setDrawEntryLabels(false);

        Description descriptionSpend = pieChart_spends.getDescription();
        descriptionSpend.setText(getString(R.string.reportGraphicsFragment_spendDescription));
        pieChart_spends.setDescription(descriptionSpend);
        pieChart_spends.setRotationEnabled(false);
        pieChart_spends.setHoleRadius(40f);
        pieChart_spends.setTransparentCircleAlpha(120);
        pieChart_spends.setCenterText(getString(R.string.reportGraphicsFragment_spendTitle));
        pieChart_spends.setCenterTextSize(12);
        pieChart_spends.setDrawEntryLabels(false);

        // Initialize value y lists
        percentagesProfitsList = new ArrayList<>();
        percentagesSpendsList = new ArrayList<>();

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

            // Get the period components
            String begPeriodDay = begPeriodDate.substring(8, 10);
            String begPeriodMonth = begPeriodDate.substring(5, 7);
            String begPeriodYear = begPeriodDate.substring(0, 4);
            String finPeriodDay = finPeriodDate.substring(8, 10);
            String finPeriodMonth = finPeriodDate.substring(5, 7);
            String finPeriodYear = finPeriodDate.substring(0, 4);

            String separator = "/";

            // Period title
            String periodTitle = getString(R.string.reportGraphicsFragment_periodoTitle_from) + " " +
                    begPeriodDay +
                    separator +
                    begPeriodMonth +
                    separator +
                    begPeriodYear + " " +
                    getString(R.string.reportGraphicsFragment_periodoTitle_to) + " " +
                    finPeriodDay +
                    separator +
                    finPeriodMonth +
                    separator +
                    finPeriodYear;

            // Set titles
            textView_titleProfit.setText(getString(R.string.reportGraphicsFragment_profitTitle));
            textView_periodProfit.setText(periodTitle);

            textView_titleSpend.setText(getString(R.string.reportGraphicsFragment_spendTitle));
            textView_periodSpend.setText(periodTitle);

            // Show graphics
            new Task().execute(begPeriodDate, finPeriodDate);

            // Show Recycler view
            scrollview_data.setVisibility(View.VISIBLE);
        } else {
            // Show info textView and hide recycler view
            textView_info.setVisibility(View.VISIBLE);
            scrollview_data.setVisibility(View.GONE);
        }

        // Chart value click listener
        pieChart_profits.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("y: ");
                String percentageStr = e.toString().substring(pos1 + 3);

                for (int i = 0; i < categoriesTmp.getCount(); i++) {
                    if (percentagesProfitsList.get(i).getValue() == Float.parseFloat(percentageStr)) {
                        pos1 = i;
                        break;
                    }
                }

                String categoryStr = percentagesProfitsList.get(pos1).getLabel();
                Toast.makeText(getContext(), getString(R.string.reportGraphicsFragment_category) +
                                ": " + categoryStr + "\n" +
                                getString(R.string.reportGraphicsFragment_total) + ": " + percentageStr + " %",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });
        pieChart_spends.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = h.toString().indexOf("x: ");
                int pos2 = h.toString().indexOf(".");
                int categIndex = Integer.parseInt(h.toString().substring(pos1 + 3, pos2));
                int pos3 = h.toString().indexOf("y: ");
                int pos4 = h.toString().indexOf(", dataSetIndex: ");
                String percentageStr = h.toString().substring(pos3 + 3, pos4);

                String categoryStr = percentagesSpendsList.get(categIndex).getLabel();
                Toast.makeText(getContext(), getString(R.string.reportGraphicsFragment_category) +
                                ": " + categoryStr + "\n" +
                                getString(R.string.reportGraphicsFragment_total) + ": " + percentageStr + " %",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        return view;
    }

    /**
     * show data in charts.
     */
    private class Task extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar_graphicProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar_graphicProgress.setVisibility(View.GONE);

            // Create data set
            PieDataSet pieDataSetProfit = new PieDataSet(percentagesProfitsList, getString(R.string.reportGraphicsFragment_category));
            pieDataSetProfit.setSliceSpace(2);
            pieDataSetProfit.setValueTextSize(10);

            PieDataSet pieDataSetSpend = new PieDataSet(percentagesSpendsList, getString(R.string.reportGraphicsFragment_category));
            pieDataSetSpend.setSliceSpace(2);
            pieDataSetSpend.setValueTextSize(10);

            // Add colors
            ArrayList<Integer> chartColors = new ArrayList<>();
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorCyan));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorGrayLight));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorGreen));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorGray));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorMagenta));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorYellow));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorRed));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorOrangeDark));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorPink));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorTurquoiseDark));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorWhiteMint));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorBlue));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorTurquoiseDark2));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorMagentaDull));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorViolet));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorBrown));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorYellowBrown));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorMagentaDark));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorTurquoise));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorBlueLight));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorGreenDull));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorYellowDull));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorPurpleDull));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorTerracottaDull));
            chartColors.add(Objects.requireNonNull(getContext()).getColor(R.color.colorBrownGrayLight));
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

        @Override
        protected Void doInBackground(String... strings) {
            // Get Entries from database with period and instance as filter
            Cursor profits = db.getEntryProfitInDate(MainActivity.idInstance, strings[0], strings[1], 0);
            // Get Entries from database with period and instance as filter
            Cursor spends = db.getEntrySpendInDate(MainActivity.idInstance, strings[0], strings[1], 0);

            // Get categories from database with instance as filter
            Cursor categories = db.getCategoriesByInstance(MainActivity.idInstance);

            // Process categories
            ArrayList<String> categoriesNames = new ArrayList<>();
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

            return null;
        }
    }
}
