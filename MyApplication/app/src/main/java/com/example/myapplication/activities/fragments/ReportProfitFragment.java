package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;
import com.example.myapplication.activities.data.ListDataReport;

import java.util.List;
import java.util.Objects;

public class ReportProfitFragment extends Fragment {

    // Global Variables
    // Cmponents view
    private RecyclerView recyclerView_profitReport;
    private RecyclerView.Adapter adapter_profitReport;

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

        // Set recycler view component
        recyclerView_profitReport = view.findViewById(R.id.recyclerView_reportProfitFragment_profitList);
        recyclerView_profitReport.setHasFixedSize(true);
        recyclerView_profitReport.setLayoutManager(new LinearLayoutManager(view.getContext()));


        if (reportPeriod != null) {
            //TODO Extract the begining and final date from "reportPeriod" variable
            showProfitsReport(); //TODO Add begining and final date as parameter
        }

        return view;
    }

    /**
     * show data in recycler view.
     */
    private void showProfitsReport() {
        //TODO Extract correct data from db. > FILTER: profits, instance, period.
        //TODO Add list_profitReport items.
    }

}
