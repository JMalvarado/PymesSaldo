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
import java.util.Calendar;
import java.util.Objects;


public class BalanceFragment extends Fragment {

    // Instantiate view components
    private TextView textView_balance;
    private TextView textView_instanceName;
    private TextView textView_profit;
    private TextView textView_spend;
    private TextView textView_date;

    // Global variables
    private String idInstance;

    private DatabaseManager db;

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
        textView_date = view.findViewById(R.id.textView_fragmentBalance_date);

        // Set instance name as title
        SharedPreferences prefs = getActivity().getSharedPreferences("instance", Context.MODE_PRIVATE);
        String name = prefs.getString("NAME", null);
        idInstance = prefs.getString("ID", null);
        textView_instanceName.setText(name);

        // Set month and year in text view
        String monthAndYear;

        String[] monthNames = {getString(R.string.month_January),
                getString(R.string.month_February),
                getString(R.string.month_March),
                getString(R.string.month_April),
                getString(R.string.month_May),
                getString(R.string.month_June),
                getString(R.string.month_July),
                getString(R.string.month_August),
                getString(R.string.month_September),
                getString(R.string.month_October),
                getString(R.string.month_November),
                getString(R.string.month_December)};

        Calendar calendar = Calendar.getInstance();
        String month = monthNames[calendar.get(Calendar.MONTH)];
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        monthAndYear = month+", "+year;

        textView_date.setText(monthAndYear);

        // Get data
        // Global variables
        String balance = getMonthBalance();
        String ingr = Long.toString(getTotalProfit());
        String gast = Long.toString(getTotalSpend());


        // Set balance, profit asn spend on textView
        textView_balance.setText(balance);
        textView_profit.setText(ingr);
        textView_spend.setText(gast);

        // Set text color
        if (Long.parseLong(balance) < 0) {
            textView_balance.setTextColor(Color.RED);
        } else if (Long.parseLong(balance) == 0) {
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
    private long getTotalProfit() {
        ArrayList<Long> profit = db.getEntryCurrentMonthIngresos(idInstance);

        long totalProfit = 0;

        for (Long integer1 : profit) {
            long ing;
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
    private long getTotalSpend() {
        ArrayList<Long> spend = db.getEntryCurrentMonthGastos(idInstance);

        long totalSpend = 0;

        for (Long integer1 : spend) {
            long gas;
            gas = integer1;
            totalSpend += gas;
        }

        return totalSpend;
    }

    /**
     * Get String with the balance (totalProfit - totalSpend) of current the month
     *
     * @return balance
     */
    private String getMonthBalance() {
        long totalProfit = getTotalProfit();
        long totalSpend = getTotalSpend();

        long balance = totalProfit - totalSpend;

        return Long.toString(balance);
    }
}
