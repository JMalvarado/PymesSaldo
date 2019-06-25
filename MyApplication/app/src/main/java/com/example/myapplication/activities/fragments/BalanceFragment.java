package com.example.myapplication.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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


public class BalanceFragment extends Fragment {

    // Instantiate view components
    private TextView textView_balance;
    private TextView textView_instanceName;
    private TextView textView_profit;
    private TextView textView_spend;

    // Global variables
    private String idInstance;

    DatabaseManager db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        // Initialize db manager instance
        db = new DatabaseManager(view.getContext());

        // Set toolbar title
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.balance_name));

        // Initialize view components
        textView_balance = view.findViewById(R.id.textView_balance);
        textView_instanceName = view.findViewById(R.id.textView_fragmentBalance_instanceName);
        textView_profit = view.findViewById(R.id.textView_fragmentBalance_insgresos);
        textView_spend = view.findViewById(R.id.textView_fragmentBalance_gastos);

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Get data
        // Global variables
        String balance = getMonthBalance();
        String ingr = Integer.toString(getTotalProfit());
        String gast = Integer.toString(getTotalSpend());


        // Set balance, profit asn spend on textView
        textView_balance.setText(balance);
        textView_profit.setText(ingr);
        textView_spend.setText(gast);

        // Set text color
        if (Integer.parseInt(balance) < 0) {
            textView_balance.setTextColor(Color.RED);
        } else if (Integer.parseInt(balance) == 0) {
            textView_balance.setTextColor(Color.BLACK);
        } else {
            textView_balance.setTextColor(Color.GREEN);
        }

        return view;
    }

    /**
     * Get integer with the total profit of the month
     *
     * @return total profit
     */
    private int getTotalProfit() {
        ArrayList<Integer> profit = db.getEntryCurrentMonthIngresos(idInstance);

        int totalProfit = 0;

        for (Integer integer1 : profit) {
            int ing;
            ing = integer1;
            totalProfit += ing;
        }

        return totalProfit;
    }

    /**
     * Get integer with the total spend of the month
     *
     * @return spend
     */
    private int getTotalSpend() {
        ArrayList<Integer> spend = db.getEntryCurrentMonthGastos(idInstance);

        int totalSpend = 0;

        for (Integer integer1 : spend) {
            int gas;
            gas = integer1;
            totalSpend += gas;
        }

        return totalSpend;
    }

    /**
     * Get String with the balance (totalProfit - totalSpend) of the month
     *
     * @return balance
     */
    private String getMonthBalance() {
        int totalProfit = getTotalProfit();
        int totalSpend = getTotalSpend();

        int balance = totalProfit - totalSpend;

        return Integer.toString(balance);
    }
}
