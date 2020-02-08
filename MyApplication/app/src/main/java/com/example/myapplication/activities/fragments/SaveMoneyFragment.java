package com.example.myapplication.activities.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.activities.MainActivity;
import com.example.myapplication.activities.data.DatabaseManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Objects;

public class SaveMoneyFragment extends Fragment {

    // View components
    private TextView textView_instanceName;
    private TextView textView_money;

    // Global variables
    private String idInstance;
    private DatabaseManager db;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save_money, container, false);

        // Initialize db
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.save_name));

        textView_money = view.findViewById(R.id.textView_fragmentSaveMoney_money);
        textView_instanceName = view.findViewById(R.id.textView_fragmentSaveMoney_instanceName);

        // Show FAB add entry from screen
        MainActivity.fab_addEntry.show();

        // Set instance name as title
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Get save money from database
        double saveBalance = getTotalSaveMoney();

        // Set in text save balance in text view

        // Format
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("###,###.##", symbols);
        String balanceDf = df.format(saveBalance);
        // Set saving balance text view
        textView_money.setText(balanceDf);

        return view;
    }

    /**
     * Get double with the total balance of the saved money
     *
     * @return total saved money
     */
    private double getTotalSaveMoney() {
        ArrayList<Double> payments;
        ArrayList<Double> withdrawalls;

        payments = db.getSaveAllPayment(idInstance);
        withdrawalls = db.getSaveAllWithdrawal(idInstance);

        double totalPayment = 0;
        double totalWithdrawall = 0;

        for (Double integer1 : payments) {
            double payment;
            payment = integer1;
            totalPayment += payment;
        }

        for (Double integer2 : withdrawalls) {
            double withdrawall;
            withdrawall = integer2;
            totalWithdrawall += withdrawall;
        }

        return totalPayment - totalWithdrawall;
    }
}
