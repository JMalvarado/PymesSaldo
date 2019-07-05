package com.example.myapplication.activities.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SaveMoneyFragment extends Fragment {

    // View components
    private TextView textView_instanceName;
    private TextView textView_money;

    // Global variables
    private String idInstance;
    private DatabaseManager db;

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

        // Set instance name as title
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Get save money from database
        int saveBalance = getTotalSaveMoney();
        // Set in text save balance in text view
        textView_money.setText(Integer.toString(saveBalance));

        return view;
    }

    /**
     * Get integer with the total balance of the saved money
     *
     * @return total saved money
     */
    private int getTotalSaveMoney() {
        ArrayList<Integer> payments;
        ArrayList<Integer> withdrawalls;

        payments = db.getSaveAllPayment(idInstance);
        withdrawalls = db.getSaveAllWithdrawal(idInstance);

        int totalPayment = 0;
        int totalWithdrawall = 0;

        for (Integer integer1 : payments) {
            int payment;
            payment = integer1;
            totalPayment += payment;
        }

        for (Integer integer2 : withdrawalls) {
            int withdrawall;
            withdrawall = integer2;
            totalWithdrawall += withdrawall;
        }

        return totalPayment-totalWithdrawall;
    }
}
