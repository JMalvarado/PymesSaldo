package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.AdapterReport;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataReport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ReportProfitFragment extends Fragment {

    // Global Variables
    // Cmponents view
    private RecyclerView recyclerView_profitReport;
    private RecyclerView.Adapter adapter_profitReport;
    private TextView textView_info;

    // Others
    private List<ListDataReport> list_profitReport;

    // Database manager instance
    private DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_profit, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // If there is default period previously selected, set that period
        // Get the shared preferences period of instance
        SharedPreferences prefsReportPeriod = Objects.requireNonNull(getActivity()).getSharedPreferences("profilereportperiod", Context.MODE_PRIVATE);
        String reportPeriod = prefsReportPeriod.getString(MainActivity.idInstance, null);

        // Set components
        textView_info = view.findViewById(R.id.textView_reportProfitFragment_info);
        // Set recycler view component
        recyclerView_profitReport = view.findViewById(R.id.recyclerView_reportProfitFragment_profitList);
        recyclerView_profitReport.setHasFixedSize(true);
        recyclerView_profitReport.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if (reportPeriod != null) {
            // Hide info textView
            textView_info.setVisibility(View.GONE);

            // Get the default period.
            String begPeriodDate = reportPeriod.substring(0, 10);
            String finPeriodDate = reportPeriod.substring(11, 21);
            showProfitsReport(begPeriodDate, finPeriodDate);

            // Show Recycler view
            recyclerView_profitReport.setVisibility(View.VISIBLE);
        } else {
            // Show info textView and hide recycler view
            textView_info.setVisibility(View.VISIBLE);
            recyclerView_profitReport.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * show data in recycler view.
     */
    private void showProfitsReport(String begDate, String finDate) {
        // Get Entries from database with period and instance as filter
        Cursor profits = db.getEntryProfitInDate(MainActivity.idInstance, begDate, finDate, 0);

        // Get categories from database with instance as filter
        Cursor categories = db.getCategoriesByInstance(MainActivity.idInstance);

        // Process categories
        ArrayList<String> categoriesIds = new ArrayList<>();
        ArrayList<String> categoriesNames = new ArrayList<>();
        ArrayList<String> categoriesIc = new ArrayList<>();
        ArrayList<Double> categoriesTotalAmount = new ArrayList<>();
        while (categories.moveToNext()) {
            double totalAmount = 0;
            String categId = categories.getString(0);
            while (profits.moveToNext()) {
                String profitCategId = profits.getString(2);
                if (profitCategId.equals(categId)) {
                    double amount = profits.getDouble(5);
                    totalAmount += amount;
                }
            }

            // Add data in temporal lists
            categoriesIds.add(categId);
            categoriesIc.add(categories.getString(2));
            categoriesNames.add(categories.getString(1));
            categoriesTotalAmount.add(totalAmount);

            // Reset profits Cursor position
            profits.moveToPosition(-1);
        }
        categories.moveToPosition(-1);

        // Get total amount to calculate percentage
        double total = 0;
        for (int i = 0; i < categoriesTotalAmount.size(); i++) {
            total += categoriesTotalAmount.get(i);
        }
        // Calculate percentage for each category
        // Add data in custom list
        list_profitReport = new ArrayList<>();
        for (int i = 0; i < categoriesIds.size(); i++) {
            assert categoriesIds != null;
            // Calculate percentage
            double totalAmountCategory = categoriesTotalAmount.get(i);
            double percentage = (totalAmountCategory * 100) / (total);

            // Add data in custom list
            ListDataReport listDataReport = new ListDataReport(
                    categoriesIds.get(i),
                    categoriesNames.get(i),
                    percentage,
                    categoriesTotalAmount.get(i),
                    categoriesIc.get(i)
            );

            list_profitReport.add(listDataReport);
        }

        // Sort List in descending mode by percentage
        list_profitReport.sort(Comparator.comparing(ListDataReport::getPercentage).reversed());

        // Set Adapter to recycler view
        adapter_profitReport = new AdapterReport(list_profitReport, getContext(), getActivity(), "I", begDate, finDate);
        recyclerView_profitReport.setAdapter(adapter_profitReport);
    }

}
